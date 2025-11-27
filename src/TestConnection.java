import java.sql.Connection;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("Testing database connection...");
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("✓ Database connection successful!");
                
                // Test user registration
                User testUser = new User("testuser", "testpass");
                if (testUser.register()) {
                    System.out.println("✓ User registration test successful!");
                } else {
                    System.out.println("✗ User registration test failed (might already exist)");
                }
                
                // Test user login
                User loginUser = User.login("testuser", "testpass");
                if (loginUser != null) {
                    System.out.println("✓ User login test successful!");
                } else {
                    System.out.println("✗ User login test failed");
                }
                
                conn.close();
            } else {
                System.out.println("✗ Database connection failed!");
            }
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
