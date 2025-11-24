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
    setSize(450, 550);
    setLocationRelativeTo(null);
    setResizable(false);
    setUndecorated(false);

    // Main Panel dengan background
    JPanel mainPanel = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Gradient background biru seperti game Flappy Bird
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        Color color1 = new Color(135, 206, 235); // Sky blue
        Color color2 = new Color(100, 180, 220);
        GradientPaint gradient = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
      }
    };
    mainPanel.setLayout(null);

    // Title Panel
    JPanel titlePanel = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(255, 200, 0));
        g2d.fillOval(150, 10, 150, 120);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawOval(150, 10, 150, 120);
      }
    };
    titlePanel.setOpaque(false);
    titlePanel.setBounds(0, 10, 450, 150);

    JLabel titleLabel = new JLabel("FLAPPY BIRD");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
    titleLabel.setForeground(Color.WHITE);
    titleLabel.setBounds(80, 50, 300, 40);
    titlePanel.add(titleLabel);

    JLabel subtitleLabel = new JLabel("LOGIN");
    subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
    subtitleLabel.setForeground(new Color(255, 200, 0));
    subtitleLabel.setBounds(150, 90, 150, 30);
    titlePanel.add(subtitleLabel);

    // Form Panel
    JPanel formPanel = new JPanel();
    formPanel.setOpaque(false);
    formPanel.setLayout(null);
    formPanel.setBounds(50, 170, 350, 250);

    // Username Label
    JLabel usernameLabel = new JLabel("Username:");
    usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
    usernameLabel.setForeground(Color.WHITE);
    usernameLabel.setBounds(0, 0, 100, 25);
    formPanel.add(usernameLabel);

    // Username Field
    usernameField = new JTextField();
    usernameField.setBounds(0, 25, 350, 35);
    usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
    usernameField.setMargin(new Insets(5, 10, 5, 10));
    usernameField.setBackground(new Color(255, 255, 255));
    usernameField.setForeground(Color.BLACK);
    usernameField.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));
    formPanel.add(usernameField);

    // Password Label
    JLabel passwordLabel = new JLabel("Password:");
    passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
    passwordLabel.setForeground(Color.WHITE);
    passwordLabel.setBounds(0, 75, 100, 25);
    formPanel.add(passwordLabel);

    // Password Field
    passwordField = new JPasswordField();
    passwordField.setBounds(0, 100, 350, 35);
    passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
    passwordField.setMargin(new Insets(5, 10, 5, 10));
    passwordField.setBackground(new Color(255, 255, 255));
    passwordField.setForeground(Color.BLACK);
    passwordField.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));
    formPanel.add(passwordField);

    // Status Label
    statusLabel = new JLabel("");
    statusLabel.setFont(new Font("Arial", Font.ITALIC, 11));
    statusLabel.setForeground(new Color(255, 100, 100));
    statusLabel.setBounds(0, 185, 350, 20);
    formPanel.add(statusLabel);

    mainPanel.add(titlePanel);
    mainPanel.add(formPanel);

    // Button Panel
    JPanel buttonPanel = new JPanel();
    buttonPanel.setOpaque(false);
    buttonPanel.setLayout(null);
    buttonPanel.setBounds(50, 430, 350, 80);

    loginButton = new JButton("LOGIN") {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        if (getModel().isPressed()) {
          g2d.setColor(new Color(255, 150, 0));
        } else if (getModel().isArmed()) {
          g2d.setColor(new Color(255, 180, 0));
        } else {
          g2d.setColor(new Color(255, 200, 0));
        }
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        super.paintComponent(g);
      }
    };
    loginButton.setBounds(0, 0, 160, 40);
    loginButton.setFont(new Font("Arial", Font.BOLD, 14));
    loginButton.setForeground(Color.BLACK);
    loginButton.setContentAreaFilled(false);
    loginButton.setBorderPainted(false);
    loginButton.setFocusPainted(false);
    loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    loginButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        handleLogin();
      }
    });
    buttonPanel.add(loginButton);

    registerButton = new JButton("REGISTER") {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        if (getModel().isPressed()) {
          g2d.setColor(new Color(100, 150, 200));
        } else if (getModel().isArmed()) {
          g2d.setColor(new Color(120, 170, 220));
        } else {
          g2d.setColor(new Color(135, 206, 235));
        }
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        super.paintComponent(g);
      }
    };
    registerButton.setBounds(190, 0, 160, 40);
    registerButton.setFont(new Font("Arial", Font.BOLD, 14));
    registerButton.setForeground(Color.WHITE);
    registerButton.setContentAreaFilled(false);
    registerButton.setBorderPainted(false);
    registerButton.setFocusPainted(false);
    registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    registerButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        handleRegister();
      }
    });
    buttonPanel.add(registerButton);

    // Exit Button
    JButton exitButton = new JButton("EXIT") {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        if (getModel().isPressed()) {
          g2d.setColor(new Color(200, 100, 100));
        } else if (getModel().isArmed()) {
          g2d.setColor(new Color(220, 120, 120));
        } else {
          g2d.setColor(new Color(240, 140, 140));
        }
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        super.paintComponent(g);
      }
    };
    exitButton.setBounds(85, 50, 180, 35);
    exitButton.setFont(new Font("Arial", Font.BOLD, 12));
    exitButton.setForeground(Color.WHITE);
    exitButton.setContentAreaFilled(false);
    exitButton.setBorderPainted(false);
    exitButton.setFocusPainted(false);
    exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    exitButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });
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
      JOptionPane.showMessageDialog(this, "Silakan isi username dan password!",
          "Peringatan", JOptionPane.WARNING_MESSAGE);
    } else if (username.length() < 3) {
      statusLabel.setText("Username minimal 3 karakter!");
      statusLabel.setForeground(new Color(255, 100, 100));
    } else if (password.length() < 6) {
      statusLabel.setText("Password minimal 6 karakter!");
      statusLabel.setForeground(new Color(255, 100, 100));
    } else {
      statusLabel.setText("Login berhasil!");
      statusLabel.setForeground(new Color(100, 255, 100));
      JOptionPane.showMessageDialog(this,
          "Selamat datang, " + username + "!",
          "Login Berhasil", JOptionPane.INFORMATION_MESSAGE);
      clearFields();
    }
  }

  private void handleRegister() {
    String username = usernameField.getText().trim();
    String password = new String(passwordField.getPassword());

    if (username.isEmpty() || password.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Silakan isi semua field terlebih dahulu!",
          "Peringatan", JOptionPane.WARNING_MESSAGE);
    } else if (username.length() < 3) {
      JOptionPane.showMessageDialog(this, "Username minimal 3 karakter!",
          "Peringatan", JOptionPane.WARNING_MESSAGE);
    } else if (password.length() < 6) {
      JOptionPane.showMessageDialog(this, "Password minimal 6 karakter!",
          "Peringatan", JOptionPane.WARNING_MESSAGE);
    } else {
      JOptionPane.showMessageDialog(this,
          "Registrasi berhasil!\nUsername: " + username + "\nSilakan login sekarang.",
          "Registrasi Berhasil", JOptionPane.INFORMATION_MESSAGE);
      clearFields();
    }
  }

  private void clearFields() {
    usernameField.setText("");
    passwordField.setText("");
    statusLabel.setText("");
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        new LoginFrame();
      }
    });
  }
}
