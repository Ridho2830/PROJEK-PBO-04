import java.sql.*;

public class DatabaseConnection {
    private static final String SERVER_URL = "jdbc:mysql://localhost:3306/";
    private static final String DATABASE_NAME = "db_flappy_bird";
    private static final String FULL_URL = SERVER_URL + DATABASE_NAME;
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    
    static {
        try {
            // Load MySQL driver explicitly
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL driver loaded successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL driver not found: " + e.getMessage());
            e.printStackTrace();
        }
        initializeDatabase();
    }
    
    private static void initializeDatabase() {
        try {
            // Coba koneksi ke server MySQL tanpa database spesifik
            Connection serverConn = DriverManager.getConnection(SERVER_URL, USERNAME, PASSWORD);
            
            // Buat database jika belum ada
            Statement stmt = serverConn.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME);
            serverConn.close();
            
            // Koneksi ke database yang baru dibuat
            Connection dbConn = DriverManager.getConnection(FULL_URL, USERNAME, PASSWORD);
            
            // Buat tabel users jika belum ada
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "username VARCHAR(50) UNIQUE NOT NULL," +
                "password VARCHAR(255) NOT NULL," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
            
            // Buat tabel scores jika belum ada
            String createScoresTable = "CREATE TABLE IF NOT EXISTS scores (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "user_id INT NOT NULL," +
                "score INT NOT NULL," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                ")";
            
            Statement dbStmt = dbConn.createStatement();
            dbStmt.executeUpdate(createUsersTable);
            dbStmt.executeUpdate(createScoresTable);
            
            dbConn.close();
            System.out.println("Database initialized successfully!");
            
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(FULL_URL, USERNAME, PASSWORD);
    }
}
