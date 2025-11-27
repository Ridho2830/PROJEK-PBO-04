import java.sql.*;
import java.util.*;

public class Score {
    private int id;
    private int userId;
    private String username;
    private int score;
    
    public Score() {}
    
    public Score(int userId, int score) {
        this.userId = userId;
        this.score = score;
    }
    
    public Score(int id, int userId, String username, int score) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.score = score;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    
    // Database Methods
    public boolean save() {
        String sql = "INSERT INTO scores (user_id, score) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, this.userId);
            stmt.setInt(2, this.score);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static List<Score> getTopScores(int limit) {
        List<Score> scores = new ArrayList<>();
        String sql = "SELECT s.id, s.user_id, u.username, s.score " +
                    "FROM scores s JOIN users u ON s.user_id = u.id " +
                    "ORDER BY s.score DESC LIMIT ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                scores.add(new Score(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getInt("score")
                ));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scores;
    }
    
    public static int getUserBestScore(int userId) {
        String sql = "SELECT MAX(score) FROM scores WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
