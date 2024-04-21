package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;



public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/marketplace?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "01102003:Amine";
    private Connection connection = null;

    public void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            initializeTables();
        }
    }

    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    private void initializeTables() throws SQLException {
        Statement statement = connection.createStatement();

        // Create the users table if it doesn't exist
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "username VARCHAR(50)," +
                "password VARCHAR(50)," +
                "firstName VARCHAR(50)," +
                "lastName VARCHAR(50)," +
                "itemsBought VARCHAR(255)," +
                "itemsPosted VARCHAR(255)" +
                ");";
        statement.executeUpdate(createUsersTable);

        // Create the items table if it doesn't exist
        String createItemsTable = "CREATE TABLE IF NOT EXISTS items (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(50)," +
                "description VARCHAR(255)," +
                "price DECIMAL(10, 2)," +
                "isAvailable BOOLEAN" +
                ");";
        statement.executeUpdate(createItemsTable);

        statement.close();
    }

}