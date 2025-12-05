import java.sql.*;

public class User {
    private int id;
    private String username;
    private String password;
    private int highScore;
    private String birdColor;
    private String backgroundMode;
    private static User currentUser = null;
    
    public User() {}
    
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.highScore = 0;
        this.birdColor = "yellow";
        this.backgroundMode = "day";
    }
    
    public User(int id, String username, String password, int highScore, String birdColor, String backgroundMode) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.highScore = highScore;
        this.birdColor = birdColor != null ? birdColor : "yellow";
        this.backgroundMode = backgroundMode != null ? backgroundMode : "day";
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public int getHighScore() { return highScore; }
    public String getBirdColor() { return birdColor; }
    public String getBackgroundMode() { return backgroundMode; }
    public void setHighScore(int score) { this.highScore = score; }
    public void setBirdColor(String color) { this.birdColor = color; }
    public void setBackgroundMode(String mode) { this.backgroundMode = mode; }

    public boolean register() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            
            // Create table if not exists with all columns
            String createTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) UNIQUE NOT NULL, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "high_score INT DEFAULT 0, " +
                    "bird_color VARCHAR(10) DEFAULT 'yellow', " +
                    "background_mode VARCHAR(10) DEFAULT 'day')";
            conn.createStatement().execute(createTable);
            
            String sql = "INSERT INTO users (username, password, high_score, bird_color, background_mode) VALUES (?, ?, 0, 'yellow', 'day')";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static User login(String username, String password) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            
            // Add columns if they don't exist
            try {
                String addHighScore = "ALTER TABLE users ADD COLUMN high_score INT DEFAULT 0";
                conn.createStatement().execute(addHighScore);
            } catch (SQLException e) {}
            
            try {
                String addBirdColor = "ALTER TABLE users ADD COLUMN bird_color VARCHAR(10) DEFAULT 'yellow'";
                conn.createStatement().execute(addBirdColor);
            } catch (SQLException e) {}
            
            try {
                String addBackgroundMode = "ALTER TABLE users ADD COLUMN background_mode VARCHAR(10) DEFAULT 'day'";
                conn.createStatement().execute(addBackgroundMode);
            } catch (SQLException e) {}
            
            String sql = "SELECT id, username, password, high_score, bird_color, background_mode FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User(rs.getInt("id"), rs.getString("username"), 
                                   rs.getString("password"), rs.getInt("high_score"), 
                                   rs.getString("bird_color"), rs.getString("background_mode"));
                setCurrentUser(user);
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateHighScore(int newScore) {
        if (newScore > this.highScore) {
            this.highScore = newScore;
            try {
                Connection conn = DatabaseConnection.getConnection();
                String sql = "UPDATE users SET high_score = ? WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, newScore);
                stmt.setInt(2, this.id);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateBirdColor(String color) {
        this.birdColor = color;
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "UPDATE users SET bird_color = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, color);
            stmt.setInt(2, this.id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateBackgroundMode(String mode) {
        this.backgroundMode = mode;
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "UPDATE users SET background_mode = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, mode);
            stmt.setInt(2, this.id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isUsernameExists(String username) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
