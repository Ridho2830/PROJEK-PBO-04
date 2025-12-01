public class LoginTest {
    public static void main(String[] args) {
        System.out.println("=== Testing Login/Register Functionality ===");
        
        // Test 1: Register new user
        String testUsername = "user" + System.currentTimeMillis();
        String testPassword = "pass123";
        
        System.out.println("\n1. Testing Registration:");
        User newUser = new User(testUsername, testPassword);
        if (newUser.register()) {
            System.out.println("✓ Registration successful for: " + testUsername);
        } else {
            System.out.println("✗ Registration failed for: " + testUsername);
        }
        
        // Test 2: Login with correct credentials
        System.out.println("\n2. Testing Login (correct credentials):");
        User loginUser = User.login(testUsername, testPassword);
        if (loginUser != null) {
            System.out.println("✓ Login successful for: " + loginUser.getUsername());
        } else {
            System.out.println("✗ Login failed for: " + testUsername);
        }
        
        // Test 3: Login with wrong credentials
        System.out.println("\n3. Testing Login (wrong credentials):");
        User wrongLogin = User.login(testUsername, "wrongpass");
        if (wrongLogin == null) {
            System.out.println("✓ Login correctly rejected wrong password");
        } else {
            System.out.println("✗ Login incorrectly accepted wrong password");
        }
        
        // Test 4: Check username exists
        System.out.println("\n4. Testing Username Check:");
        if (User.isUsernameExists(testUsername)) {
            System.out.println("✓ Username existence check working");
        } else {
            System.out.println("✗ Username existence check failed");
        }
        
        // Test 5: Try duplicate registration
        System.out.println("\n5. Testing Duplicate Registration:");
        User duplicateUser = new User(testUsername, testPassword);
        if (!duplicateUser.register()) {
            System.out.println("✓ Duplicate registration correctly prevented");
        } else {
            System.out.println("✗ Duplicate registration incorrectly allowed");
        }
        
        System.out.println("\n=== Test Complete ===");
    }
}
