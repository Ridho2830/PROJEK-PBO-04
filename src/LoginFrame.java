import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {
  private JTextField usernameField;
  private JPasswordField passwordField;
  private JButton loginButton;
  private JButton registerButton;
  private JLabel statusLabel;

  public LoginFrame() {
    setTitle("Flappy Bird - Login");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(360, 640);
    setLocationRelativeTo(null);
    setResizable(false);

    JPanel mainPanel = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
          Image backgroundImg = new ImageIcon("assets/sprites/background-day.png").getImage();
          g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), null);
        } catch (Exception e) {
          Graphics2D g2d = (Graphics2D) g;
          g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          Color color1 = new Color(135, 206, 250);
          Color color2 = new Color(70, 130, 180);
          GradientPaint gradient = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
          g2d.setPaint(gradient);
          g2d.fillRect(0, 0, getWidth(), getHeight());
        }
      }
    };
    mainPanel.setLayout(null);

    // JLabel titleLabel = new JLabel("🐦 FLAPPY BIRD 🐦", SwingConstants.CENTER);
    JLabel titleLabel = new JLabel("FLAPPY BIRD", SwingConstants.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
    titleLabel.setForeground(new Color(255, 215, 0));
    titleLabel.setBounds(30, 80, 300, 50);
    mainPanel.add(titleLabel);

    JPanel formPanel = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(255, 255, 255, 30));
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        g2d.setColor(new Color(255, 255, 255, 80));
        g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
      }
    };
    formPanel.setOpaque(false);
    formPanel.setLayout(null);
    formPanel.setBounds(30, 180, 300, 200);

    JLabel userLabel = new JLabel("Username:");
    userLabel.setFont(new Font("Arial", Font.BOLD, 14));
    userLabel.setForeground(Color.WHITE);
    userLabel.setBounds(20, 20, 100, 25);
    formPanel.add(userLabel);

    usernameField = new JTextField();
    usernameField.setBounds(20, 45, 260, 35);
    usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
    usernameField.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createRaisedBevelBorder(),
        BorderFactory.createEmptyBorder(5, 10, 5, 10)));
    formPanel.add(usernameField);

    JLabel passLabel = new JLabel("Password:");
    passLabel.setFont(new Font("Arial", Font.BOLD, 14));
    passLabel.setForeground(Color.WHITE);
    passLabel.setBounds(20, 90, 100, 25);
    formPanel.add(passLabel);

    passwordField = new JPasswordField();
    passwordField.setBounds(20, 115, 260, 35);
    passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
    passwordField.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createRaisedBevelBorder(),
        BorderFactory.createEmptyBorder(5, 10, 5, 10)));
    formPanel.add(passwordField);

    statusLabel = new JLabel("", SwingConstants.CENTER);
    statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
    statusLabel.setForeground(new Color(255, 255, 100));
    statusLabel.setBounds(20, 160, 260, 25);
    formPanel.add(statusLabel);

    mainPanel.add(formPanel);

    JPanel buttonPanel = new JPanel();
    buttonPanel.setOpaque(false);
    buttonPanel.setLayout(null);
    buttonPanel.setBounds(30, 420, 300, 50);

    int buttonWidth = 86;
    int buttonHeight = 40;
    int spacing = 4;

    loginButton = new JButton("LOGIN");
    loginButton.setBounds(0, 0, buttonWidth, buttonHeight);
    loginButton.setFont(new Font("Arial", Font.BOLD, 12));
    loginButton.setBackground(new Color(76, 175, 80));
    loginButton.setForeground(Color.WHITE);
    loginButton.setBorder(BorderFactory.createRaisedBevelBorder());
    loginButton.setFocusPainted(false);
    loginButton.addActionListener(e -> handleLogin());
    buttonPanel.add(loginButton);

    registerButton = new JButton("REGISTER");
    registerButton.setBounds(buttonWidth + spacing, 0, buttonWidth, buttonHeight);
    registerButton.setFont(new Font("Arial", Font.BOLD, 12));
    registerButton.setBackground(new Color(33, 150, 243));
    registerButton.setForeground(Color.WHITE);
    registerButton.setBorder(BorderFactory.createRaisedBevelBorder());
    registerButton.setFocusPainted(false);
    registerButton.addActionListener(e -> handleRegister());
    buttonPanel.add(registerButton);

    JButton exitButton = new JButton("EXIT");
    exitButton.setBounds((buttonWidth + spacing) * 2, 0, buttonWidth, buttonHeight);
    exitButton.setFont(new Font("Arial", Font.BOLD, 12));
    exitButton.setBackground(new Color(244, 67, 54));
    exitButton.setForeground(Color.WHITE);
    exitButton.setBorder(BorderFactory.createRaisedBevelBorder());
    exitButton.setFocusPainted(false);
    exitButton.addActionListener(e -> System.exit(0));
    buttonPanel.add(exitButton);

    mainPanel.add(buttonPanel);
    add(mainPanel);
    setVisible(true);
  }

  private void handleLogin() {
    String username = usernameField.getText().trim();
    String password = new String(passwordField.getPassword());

    if (username.isEmpty() || password.isEmpty()) {
      statusLabel.setText("⚠️ Username dan password tidak boleh kosong!");
      statusLabel.setForeground(new Color(255, 100, 100));
      return;
    }

    try {
      User user = User.login(username, password);
      if (user != null) {
        statusLabel.setText("✅ Login berhasil!");
        statusLabel.setForeground(new Color(100, 255, 100));

        // Open Main Menu instead of direct game
        new MainMenu(user);
        this.dispose();
      } else {
        statusLabel.setText("❌ Username atau password salah!");
        statusLabel.setForeground(new Color(255, 100, 100));
      }
    } catch (Exception e) {
      statusLabel.setText("Error koneksi database: " + e.getMessage());
      statusLabel.setForeground(new Color(255, 100, 100));
      e.printStackTrace();
    }
  }

  private void handleRegister() {
    String username = usernameField.getText().trim();
    String password = new String(passwordField.getPassword());

    if (username.isEmpty() || password.isEmpty()) {
      statusLabel.setText("Silakan isi semua field!");
      statusLabel.setForeground(new Color(255, 100, 100));
      return;
    }

    if (username.length() < 3) {
      statusLabel.setText("Username minimal 3 karakter!");
      statusLabel.setForeground(new Color(255, 100, 100));
      return;
    }

    if (password.length() < 3) {
      statusLabel.setText("Password minimal 3 karakter!");
      statusLabel.setForeground(new Color(255, 100, 100));
      return;
    }

    try {
      if (User.isUsernameExists(username)) {
        statusLabel.setText("Username sudah digunakan!");
        statusLabel.setForeground(new Color(255, 100, 100));
        return;
      }

      User newUser = new User(username, password);
      if (newUser.register()) {
        statusLabel.setText("Registrasi berhasil! Silakan login.");
        statusLabel.setForeground(new Color(100, 255, 100));
        clearFields();
      } else {
        statusLabel.setText("Registrasi gagal!");
        statusLabel.setForeground(new Color(255, 100, 100));
      }
    } catch (Exception e) {
      statusLabel.setText("Error koneksi database: " + e.getMessage());
      statusLabel.setForeground(new Color(255, 100, 100));
      e.printStackTrace();
    }
  }

  private void clearFields() {
    usernameField.setText("");
    passwordField.setText("");
  }

  private void openFlappyBirdGame() {
    JFrame gameFrame = new JFrame("Flappy Bird");
    gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    gameFrame.setSize(360, 640);
    gameFrame.setLocationRelativeTo(null);
    gameFrame.setResizable(false);

    FlappyBird flappyBird = new FlappyBird();
    gameFrame.add(flappyBird);
    gameFrame.pack();
    gameFrame.setVisible(true);
    flappyBird.requestFocus();
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new LoginFrame());
  }
}
