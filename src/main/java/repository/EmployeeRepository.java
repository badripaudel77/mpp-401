package repository;

// DAO for employee

import config.DBConfig;
import model.Department;
import model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeRepository {
    static Connection connection;

    static {
        connection = DBConfig.getConnection();
    }

    // INSERT employee
    public void insert(Employee employee) throws SQLException {
        String sql = "INSERT INTO employee (full_name, email, title, hire_date, salary, department_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, employee.getFullName());
            ps.setString(2, employee.getEmail());
            ps.setString(3, employee.getTitle());
            ps.setDate(4, Date.valueOf(employee.getHireDate()));
            ps.setDouble(5, employee.getSalary());
            ps.setInt(6, employee.getDepartment().getId());
            ps.executeUpdate();
        }
        System.out.println("Employee " + employee.getFullName() + " added and assigned to the department " +  Optional.of(employee.getDepartment().getId()).orElse(-1111));
    }

    public Employee findById(Integer id) {
        var query = "SELECT * FROM Employee WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return mapToEmployee(resultSet);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public List<Employee> findAll() throws SQLException {
        String query = "SELECT * FROM Employee";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            var employees = mapToEmployeeList(resultSet);
            System.out.println("All employees fetched ");
            return employees;
        }
        catch (SQLException e) {
            System.out.println("Something went wrong : " + e.getMessage());
        }
        return List.of();
    }
    public void update(Employee employee, Integer id) throws SQLException {
        String sql = "UPDATE employee SET full_name=?, email=?, title=?, hire_date=?, salary=?, department_id=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, employee.getFullName());
            ps.setString(2, employee.getEmail());
            ps.setString(3, employee.getTitle());
            ps.setDate(4, Date.valueOf(employee.getHireDate()));
            ps.setDouble(5, employee.getSalary());
            ps.setInt(6, employee.getDepartment().getId());
            ps.setInt(7, id);
            ps.executeUpdate();
        }
        System.out.println("Employee with ID : " + id + " updated.");
    }

    // DELETE employee
    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM employee WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Employee with ID: " + id + " deleted.");
            }
            else {
                System.out.println("No employee found with ID: " + id);
            }
        }
        catch (SQLException exception) {
            System.out.println("Something went wrong while deleting ID: " + id + " error : " + exception.getMessage());
        }
    }


    // FIND by department's ID
    public List<Employee> findByDepartment(Long id) throws SQLException {
        String sql = "SELECT e.* FROM employee e " +
                "JOIN department d ON e.department_id = d.id " +
                "WHERE d.id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                var employees = mapToEmployeeList(rs);
                System.out.println("Employee in department : " + id + " fetched.");
                return employees;
            }
            catch (SQLException exception) {
                System.out.println("Something went wrong : " + exception.getMessage());
            }
        }
        return List.of();
    }

    private Employee mapToEmployee(ResultSet resultSet) throws SQLException {
        Employee employee;
        var depId = resultSet.getInt(7);
        employee = new Employee(resultSet.getInt(1), resultSet.getString(2),
                resultSet.getString(3), resultSet.getString(4), resultSet.getDate(5).toLocalDate(),
                resultSet.getDouble(6));
        employee.setDepartment(new Department(depId));
        return employee;
    }

    private List<Employee> mapToEmployeeList(ResultSet resultSet) throws SQLException {
        List<Employee> employees = new ArrayList<>();
        while (resultSet.next()) {
            var employee = new Employee(resultSet.getInt(1), resultSet.getString(2),
                    resultSet.getString(3), resultSet.getString(4), resultSet.getDate(5).toLocalDate(),
                    resultSet.getDouble(6));
            var depId = resultSet.getInt(7);
            employee.setDepartment(new Department(depId));
            employees.add(employee);
        }
        return employees;
    }
}
