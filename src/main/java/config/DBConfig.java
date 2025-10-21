package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static constants.DBConstants.*;

public class DBConfig {
    public static Connection getConnection() {
        try {
            Class.forName(DB_CLASSNAME);
            return DriverManager.getConnection(
                    DB_CONN_URL,
                    DB_USERNAME,
                    DB_PASSWORD
            );
        }
        catch (ClassNotFoundException | SQLException e) {
            System.out.println("Exception occurred : " + e.getMessage());
            throw new RuntimeException("Either JDBC Driver not found or Failed to connect to the database.", e);
        }
    }
}
