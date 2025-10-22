package repository;

import config.DBConfig;
import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class ClientRepository {

    private static final Connection connection;

    static {
        connection = DBConfig.getConnection();
    }

    // CREATE
    public void insert(Client client) throws SQLException {
        String sql = "INSERT INTO client (name, industry, contact_person, contact_phone, contact_email) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, client.getName());
            ps.setString(2, client.getIndustry());
            ps.setString(3, client.getContactPerson());
            ps.setString(4, client.getContactPhone());
            ps.setString(5, client.getContactEmail());

            ps.executeUpdate();
            System.out.println("Client added to the database.");
        }
    }

    // Find all clients
    public List<Client> findAll() throws SQLException {
        String sql = "SELECT * FROM client";
        List<Client> clients = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                clients.add(mapRowToClient(rs));
            }
        }
        return clients;
    }

    // FIND BY ID
    public Client findById(Long id) throws SQLException {
        String sql = "SELECT * FROM client WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    var client = mapRowToClient(rs);
                    // populate projects for given client
                    var projects = findProjectsByClientId(client.getId());
                    client.setProjects(projects);
                    return client;
                }
            }
        }
        return null;
    }

    // UPDATE
    public void update(Client client, Long id) throws SQLException {
        String sql = "UPDATE client SET name=?, industry=?, contact_person=?, contact_phone = ?, contact_email =? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, client.getName());
            ps.setString(2, client.getIndustry());
            ps.setString(3, client.getContactPerson());
            ps.setString(4, client.getContactPhone());
            ps.setString(5, client.getContactEmail());
            ps.setLong(4, id);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("No client found with ID: " + id);
            }
        }
    }

    // DELETE
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM client WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            int rowsAffected = ps.executeUpdate();
            Optional.of(rowsAffected > 0)
                    .map(deleted -> deleted ? "Client deleted: " + id : "No client found with ID: " + id)
                    .ifPresent(System.out::println);
        }
    }

    // HELPER method: map ResultSet to Client
    private Client mapRowToClient(ResultSet rs) throws SQLException {
        Client client = new Client();
        client.setId(rs.getLong("id"));
        client.setName(rs.getString("name"));
        client.setIndustry(rs.getString("industry"));
        client.setContactPerson(rs.getString("contact_person"));
        client.setContactPhone(rs.getString("contact_phone"));
        client.setContactEmail(rs.getString("contact_email"));
        return client;
    }

    // Optional: fetch projects of a department
    private List<Project> findProjectsByClientId(Long clientId) throws SQLException {
        String sql = """
                SELECT p.*
                FROM project p
                JOIN project_client pc ON p.id = pc.project_id
                WHERE pc.client_id = ?
                """;
        List<Project> projects = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, clientId);
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

