package repository;

import config.DBConfig;
import model.Department;
import model.Employee;
import model.Project;
import model.ProjectStatus;
import service.impl.EmployeeService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class DepartmentRepository {

    private static final Connection connection;

    static {
        connection = DBConfig.getConnection();
    }

    // CREATE
    public void insert(Department dept) throws SQLException {
        String sql = "INSERT INTO department (name, location, annual_budget) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, dept.getName());
            ps.setString(2, dept.getLocation());
            ps.setDouble(3, dept.getAnnualBudget());
            System.out.println("Department created.");
        }
    }

    // READ ALL
    public List<Department> findAll() throws SQLException {
        String sql = "SELECT * FROM department";
        List<Department> departments = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                departments.add(mapRowToDepartment(rs));
            }
        }
        return departments;
    }

    // FIND BY ID
    public Department findById(Long id) throws SQLException {
        String sql = "SELECT * FROM department WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Department dept = mapRowToDepartment(rs);
                    // populate employees and projects
                    dept.setEmployees(findEmployeesByDepartmentId(id));
                    dept.setProjects(findProjectsByDepartmentId(id));
                    return dept;
                }
            }
        }
        return null;
    }

    // UPDATE
    public void update(Department dept, Long id) throws SQLException {
        String sql = "UPDATE department SET name=?, location=?, annual_budget=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, dept.getName());
            ps.setString(2, dept.getLocation());
            ps.setDouble(3, dept.getAnnualBudget());
            ps.setLong(4, id);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) System.out.println("No department found with ID: " + dept.getId());
        }
    }

    // DELETE
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM department WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            int rowsAffected = ps.executeUpdate();
            Optional.of(rowsAffected > 0)
                    .map(deleted -> deleted ? "Department deleted: " + id : "No department found with ID: " + id)
                    .ifPresent(System.out::println);
        }
    }

    // HELPER method: map ResultSet to Department
    private Department mapRowToDepartment(ResultSet rs) throws SQLException {
        Department dept = new Department();
        dept.setId(rs.getInt("id"));
        dept.setName(rs.getString("name"));
        dept.setLocation(rs.getString("location"));
        dept.setAnnualBudget(rs.getDouble("annual_budget"));
        return dept;
    }

    // get Employee by dep ID
    private List<Employee> findEmployeesByDepartmentId(Long deptId) throws SQLException {
        return new EmployeeService().findByDepartment(deptId);
    }

    // Optional: fetch projects of a department
    private List<Project> findProjectsByDepartmentId(Long deptId) throws SQLException {
        String sql = """
                SELECT p.*
                FROM project p
                JOIN department_project pd ON p.id = pd.project_id
                WHERE pd.department_id = ?
                """;
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

