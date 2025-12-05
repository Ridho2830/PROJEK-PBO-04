import java.sql.*;

public class DatabaseConnection {
    private static final String SERVER_URL = "jdbc:mysql://localhost:3306/";
    private static final String DATABASE_NAME = "db_flappy_bird";
    private static final String FULL_URL = SERVER_URL + DATABASE_NAME;
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL driver loaded successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL driver not found: " + e.getMessage());
        }
        initializeDatabase();
    }
    
    private static void initializeDatabase() {
        try {
            try (Connection serverConn = DriverManager.getConnection(SERVER_URL, USERNAME, PASSWORD);
                 Statement stmt = serverConn.createStatement()) {
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME);
            }
            try (Connection dbConn = DriverManager.getConnection(FULL_URL, USERNAME, PASSWORD);
                 Statement dbStmt = dbConn.createStatement()) {

                String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "username VARCHAR(50) UNIQUE NOT NULL," +
                    "password VARCHAR(255) NOT NULL," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
                
                String createScoresTable = "CREATE TABLE IF NOT EXISTS scores (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "user_id INT NOT NULL," +
                    "score INT NOT NULL," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                    ")";
                
                dbStmt.executeUpdate(createUsersTable);
                dbStmt.executeUpdate(createScoresTable);
            }
            System.out.println("Database initialized successfully!");
            
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }
    }
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(FULL_URL, USERNAME, PASSWORD);
    }
}
