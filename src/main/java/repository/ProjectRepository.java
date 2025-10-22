package repository;

import config.DBConfig;
import model.Project;
import model.ProjectStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        // find it's associated departments
        // associated clients
        return null;
    }

    public List<Project> findAll() {
        return null;
    }

    public void update(Project entity, Long id) {

    }

    public void delete(Long id) {

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

}
