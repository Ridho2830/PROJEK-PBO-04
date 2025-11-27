import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrameNew extends JFrame {
  private JTextField usernameField;
  private JPasswordField passwordField;
  private JButton loginButton;
  private JButton registerButton;
  private JLabel statusLabel;

  public LoginFrameNew() {
    setTitle("Flappy Bird - Login");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(450, 550);
    setLocationRelativeTo(null);
    setResizable(false);

    JPanel mainPanel = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        Color color1 = new Color(135, 206, 235);
        Color color2 = new Color(100, 180, 220);
        GradientPaint gradient = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
      }
    };
    mainPanel.setLayout(null);

    JLabel titleLabel = new JLabel("FLAPPY BIRD", SwingConstants.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
    titleLabel.setForeground(Color.WHITE);
    titleLabel.setBounds(50, 50, 350, 40);
    mainPanel.add(titleLabel);

    JPanel formPanel = new JPanel();
    formPanel.setOpaque(false);
    formPanel.setLayout(null);
    formPanel.setBounds(50, 150, 350, 220);

    JLabel userLabel = new JLabel("Username:");
    userLabel.setFont(new Font("Arial", Font.BOLD, 14));
    userLabel.setForeground(Color.WHITE);
    userLabel.setBounds(0, 20, 100, 25);
    formPanel.add(userLabel);

    usernameField = new JTextField();
    usernameField.setBounds(0, 45, 350, 35);
    usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
    formPanel.add(usernameField);

    JLabel passLabel = new JLabel("Password:");
    passLabel.setFont(new Font("Arial", Font.BOLD, 14));
    passLabel.setForeground(Color.WHITE);
    passLabel.setBounds(0, 90, 100, 25);
    formPanel.add(passLabel);

    passwordField = new JPasswordField();
    passwordField.setBounds(0, 115, 350, 35);
    passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
    formPanel.add(passwordField);

    statusLabel = new JLabel("");
    statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
    statusLabel.setForeground(new Color(255, 100, 100));
    statusLabel.setBounds(0, 160, 350, 20);
    formPanel.add(statusLabel);

    mainPanel.add(formPanel);

    JPanel buttonPanel = new JPanel();
    buttonPanel.setOpaque(false);
    buttonPanel.setLayout(null);
    buttonPanel.setBounds(50, 400, 350, 100);

    loginButton = new JButton("LOGIN");
    loginButton.setBounds(0, 0, 160, 40);
    loginButton.setFont(new Font("Arial", Font.BOLD, 14));
    loginButton.setBackground(new Color(255, 200, 0));
    loginButton.setForeground(Color.BLACK);
    loginButton.addActionListener(e -> handleLogin());
    buttonPanel.add(loginButton);

    registerButton = new JButton("REGISTER");
    registerButton.setBounds(190, 0, 160, 40);
    registerButton.setFont(new Font("Arial", Font.BOLD, 14));
    registerButton.setBackground(new Color(135, 206, 235));
    registerButton.setForeground(Color.WHITE);
    registerButton.addActionListener(e -> handleRegister());
    buttonPanel.add(registerButton);

    JButton exitButton = new JButton("EXIT");
    exitButton.setBounds(85, 50, 180, 35);
    exitButton.setFont(new Font("Arial", Font.BOLD, 12));
    exitButton.setBackground(new Color(240, 140, 140));
    exitButton.setForeground(Color.WHITE);
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
      statusLabel.setText("Username dan password tidak boleh kosong!");
      statusLabel.setForeground(new Color(255, 100, 100));
      return;
    }

    try {
      User user = User.login(username, password);
      if (user != null) {
        statusLabel.setText("Login berhasil!");
        statusLabel.setForeground(new Color(100, 255, 100));
        JOptionPane.showMessageDialog(this, "Selamat datang, " + username + "!");
        
        // TODO: Buka game window
        // new FlappyBirdGame(user);
        this.dispose();
      } else {
        statusLabel.setText("Username atau password salah!");
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

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new LoginFrameNew());
  }
}
