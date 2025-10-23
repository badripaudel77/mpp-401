package repository;

import config.DBConfig;
import model.Client;
import model.Department;
import model.Project;
import model.ProjectStatus;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class ProjectRepository {
    private static final Connection connection;

    static {
        connection = DBConfig.getConnection();
    }

    // CREATE
    public void insert(Project project) throws SQLException {
        String sql = "INSERT INTO project (name, description, start_date, end_date, budget, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, project.getName());
            ps.setString(2, project.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(project.getStartDate()));
            ps.setDate(4, java.sql.Date.valueOf(project.getEndDate()));
            ps.setDouble(5, project.getBudget());
            ps.setString(6, project.getStatus().name());

            ps.executeUpdate();
            System.out.println("Project added to the database.");
        }
    }

    public Project findById(Long id) {
        // find the project
        var query = "SELECT * FROM project WHERE id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Project project = mapRowToProject(resultSet);
                // find it's associated departments
                project.setDepartments(findDepartmentsByProjectId(id));
                // associated clients
                project.setClients(findClientsByProjectId(id));
                return project;
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public List<Project> findAll() throws SQLException{
        String sql = "SELECT * FROM project";
        List<Project> projects = new ArrayList<>();
        try (Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()){
                projects.add(mapRowToProject(resultSet));
            }
        }
        return projects;
    }

    public void update(Project entity, Long id) throws SQLException{
        String sql = "UPDATE project SET name = ?, description = ?, start_date = ?, end_date = ?, budget = ?, " +
                "status = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(7, id);

            statement.setString(1, entity.getName());
            statement.setString(2, entity.getDescription());
            statement.setDate(3, Date.valueOf(entity.getStartDate()));
            statement.setDate(4, Date.valueOf(entity.getEndDate()));
            statement.setDouble(5, entity.getBudget());
            statement.setString(6, (entity.getStatus().name()));
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("No project found with id: " + entity.getId());
            }
        }
    }

    public void delete(Long id) throws SQLException{
        String sql = "DELETE FROM project WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setLong(1, id);
            int rowsAffected = statement.executeUpdate();
            Optional.of(rowsAffected > 0)
                    .map(deleted -> deleted ? "Project deleted:" + id : "No project found with id: " + id)
                    .ifPresent(System.out::println);
        }
    }

    public double calculateProjectHRCost(int projectId) throws SQLException {
        String projectSql = "SELECT start_date, end_date FROM project WHERE id = ?";
        String allocationSql = """
                SELECT e.salary, pa.allocation_percentage
                FROM project_allocation pa
                JOIN employee e ON pa.employee_id = e.id
                WHERE pa.project_id = ?
        """;
        LocalDate startDate, endDate;

        // Fetch project duration
        try (PreparedStatement ps = connection.prepareStatement(projectSql)) {
            ps.setInt(1, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    startDate = rs.getDate("start_date").toLocalDate();
                    endDate = rs.getDate("end_date").toLocalDate();
                }
                else {
                    throw new SQLException("Project not found with ID: " + projectId);
                }
            }
        }
        // Calculate duration in months (rounded up)
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
        double months = Math.ceil(daysBetween / 30.0);
        double totalCost = 0.0;
        // Fetch all employees and compute weighted cost
        try (PreparedStatement ps = connection.prepareStatement(allocationSql)) {
            ps.setInt(1, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    double salary = rs.getDouble("salary");
                    double allocation = rs.getDouble("allocation_percentage");
                    double monthlySalary = salary / 12.0;
                    totalCost += monthlySalary * (allocation/100) * months;
                }
            }
        }
        return totalCost;
    }

    public List<Project> findProjectsByDepartmentId(Long deptId, String sortBy) throws SQLException {
        String sql = """
            SELECT p.*
            FROM project p
            JOIN department_project pd ON p.id = pd.project_id
            WHERE pd.department_id = ? AND p.status = 'Active'
            ORDER BY %s
            """.formatted("p." + sortBy);

        List<Project> projects = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, deptId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Project p = new Project();
                    p.setId(rs.getLong("id"));
                    p.setName(rs.getString("name"));
                    p.setDescription(rs.getString("description"));
                    p.setStartDate(rs.getDate("start_date").toLocalDate());
                    p.setEndDate(rs.getDate("end_date").toLocalDate());
                    p.setBudget(rs.getDouble("budget"));
                    p.setStatus(ProjectStatus.valueOf(rs.getString("status").toUpperCase(Locale.ROOT)));
                    projects.add(p);
                }
            }
        }
        return projects;
    }

    private Project mapRowToProject(ResultSet resultSet)throws SQLException{
        Project project = new Project();
        project.setId(resultSet.getLong(1));
        project.setName(resultSet.getString(2));
        project.setDescription(resultSet.getString(3));
        project.setStartDate(resultSet.getDate(4).toLocalDate());
        project.setEndDate(resultSet.getDate(5).toLocalDate());
        project.setBudget(resultSet.getDouble(6));
        project.setStatus(ProjectStatus.valueOf(resultSet.getString("status").toUpperCase(Locale.ROOT)));
        return project;
    }

    private List<Department> findDepartmentsByProjectId(Long projId) throws SQLException{
        String sql = """
                SELECT d.*
                FROM department d
                JOIN department_project pd ON d.id = pd.department_id
                WHERE pd.project_id = ?
                """;
        List<Department> departments = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, projId);
            try (ResultSet resultSet = statement.executeQuery()){
                while (resultSet.next()){
                    Department dept = new Department();
                    dept.setId(resultSet.getInt("id"));
                    dept.setName(resultSet.getString("name"));
                    dept.setLocation(resultSet.getString("location"));
                    dept.setAnnualBudget(resultSet.getDouble("annual_budget"));
                    departments.add(dept);
                }
            }
        }
        return departments;
    }

    private List<Client> findClientsByProjectId(Long projId) throws SQLException{
        String sql = """
                SELECT c.* 
                FROM client c
                JOIN project_client cp ON c.id = cp.client_id
                WHERE cp.project_id = ?
                """;
        List<Client> clients = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setLong(1, projId);
            try(ResultSet resultSet = statement.executeQuery()){
                while (resultSet.next()){
                    Client client = new Client();
                    client.setId(resultSet.getLong("id"));
                    client.setName(resultSet.getString("name"));
                    client.setIndustry(resultSet.getString("industry"));
                    client.setContactPerson(resultSet.getString("contact_person"));
                    client.setContactPhone(resultSet.getString("contact_phone"));
                    client.setContactEmail(resultSet.getString("contact_email"));
                    clients.add(client);
                }
            }
        }
        return clients;
    }

}
