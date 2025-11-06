package dataaccess.sql;

import dataaccess.DataAccessException;

import java.sql.*;
import java.util.Properties;

import static java.sql.DriverManager.println;

public class DatabaseManager {
    private static String databaseName;
    private static String dbUsername;
    private static String dbPassword;
    private static String connectionUrl;
    private static String host;
    private static int port;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        loadPropertiesFromResources();
        try {
            createDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException("Unable to create database: " + e.getMessage(), e);
        }
    }

    /**
     * Creates the database if it does not already exist.
     */
    static public void createDatabase() throws DataAccessException {
        var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
        String rootUrl = String.format("jdbc:mysql://%s:%d/", host, port);
        try (var conn = DriverManager.getConnection(rootUrl, dbUsername, dbPassword);
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create database", ex);
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DatabaseManager.getConnection()) {
     * // execute SQL statements.
     * }
     * </code>
     */
    static Connection getConnection() throws DataAccessException {
        try {
            if (port < 0 || port > 65535) {
                throw new DataAccessException("Invalid database port: " + port);
            }

            Connection conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
            conn.setCatalog(databaseName);
            conn.setAutoCommit(true);
            return conn;
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to connect to database", ex);
        }
    }

    public static void loadPropertiesFromResources() {
        try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
            if (propStream == null) {
                throw new Exception("Unable to load db.properties");
            }
            Properties props = new Properties();
            props.load(propStream);
            loadProperties(props);
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties", ex);
        }
    }

    public static void loadProperties(Properties props) {
        databaseName = props.getProperty("db.name");
        dbUsername = props.getProperty("db.user");
        dbPassword = props.getProperty("db.password");

        host = props.getProperty("db.host");

        try {
            port = Integer.parseInt(props.getProperty("db.port"));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid port number in properties", e);
        }

        connectionUrl = String.format("jdbc:mysql://%s:%d/%s", host, port, databaseName);

        System.out.println("DB config loaded: " + host + ":" + port + " / " + databaseName);
    }

    public static String  getDatabaseName() {
        return databaseName;
    }
    public static String getUsername() {
        return dbUsername;
    }

    public static String getPassword() {
        return dbPassword;
    }

    public static String getHost() {
        return host;
    }

    public static int getPort() {
        return port;
    }
}
