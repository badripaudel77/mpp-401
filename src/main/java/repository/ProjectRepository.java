package repository;

import config.DBConfig;
import model.Project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

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
}
