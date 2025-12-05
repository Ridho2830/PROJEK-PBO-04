import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.imageio.ImageIO;
import javax.swing.*;

public class LoginFrame extends JFrame {

    // Komponen UI
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel statusLabel;

    // Aset Visual
    private BufferedImage dayBackground;
    private BufferedImage nightBackground;
    private JPanel mainPanel;

    // Variabel Animasi
    private int bgOffsetX = 0;
    private final AtomicBoolean isDay = new AtomicBoolean(true);
    private final AtomicBoolean running = new AtomicBoolean(true);
    private Thread parallaxThread;
    private Thread dayNightThread;

    // Audio
    private static MusicPlayer music = new MusicPlayer();


    public LoginFrame() {
        setTitle("Flappy Bird - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(360, 640);
        setLocationRelativeTo(null);
        setResizable(false);

        // Muat gambar background
        loadBackgroundImages();

        // === MAIN PANEL (Custom Painting) ===
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Pilih background berdasarkan waktu (AtomicBoolean)
                BufferedImage bg = isDay.get() ? dayBackground : nightBackground;
                int w = getWidth();
                int h = getHeight();

                if (bg != null) {
                    // Seamless Parallax Logic (Menggambar 3 salinan agar looping mulus)
                    g2d.drawImage(bg, bgOffsetX, 0, w, h, this);
                    g2d.drawImage(bg, bgOffsetX + w, 0, w, h, this);
                    g2d.drawImage(bg, bgOffsetX - w, 0, w, h, this);
                } else {
                    // Fallback jika gambar tidak ditemukan (Gradient)
                    Color top = isDay.get() ? new Color(135, 206, 250) : new Color(15, 15, 40);
                    Color bottom = isDay.get() ? new Color(70, 130, 180) : new Color(0, 0, 20);
                    g2d.setPaint(new GradientPaint(0, 0, top, 0, h, bottom));
                    g2d.fillRect(0, 0, w, h);
                }
                g2d.dispose();
            }
        };
        mainPanel.setLayout(null);
        add(mainPanel);

        // === UI COMPONENTS ===
        
        // Judul Game
        JLabel titleLabel = new JLabel("FLAPPY BIRD", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial Black", Font.BOLD, 28));
        titleLabel.setForeground(new Color(255, 215, 0));
        // Efek shadow sedehana pada text
        titleLabel.setBorder(BorderFactory.createEmptyBorder()); 
        titleLabel.setBounds(30, 80, 300, 50);
        mainPanel.add(titleLabel);

        // Panel Form
        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel);

        // Panel Tombol
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel);

        // Jalankan Thread Animasi
        startThreads();

        setVisible(true);
        music.play("assets/audio/menu-music.wav", true);
        
    }

    // --- SETUP VISUAL ---

    private JPanel createFormPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Box Transparan
                g2d.setColor(new Color(255, 255, 255, 50)); 
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                
                // Border Putih Tipis
                g2d.setColor(new Color(255, 255, 255, 150));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
            }
        };
        panel.setOpaque(false);
        panel.setLayout(null);
        panel.setBounds(30, 180, 300, 200);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userLabel.setBounds(20, 20, 100, 25);
        panel.add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(20, 45, 260, 35);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(usernameField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passLabel.setBounds(20, 90, 100, 25);
        panel.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(20, 115, 260, 35);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(passwordField);

        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setForeground(new Color(255, 255, 100));
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusLabel.setBounds(20, 160, 260, 25);
        panel.add(statusLabel);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(null);
        panel.setBounds(30, 420, 300, 100);

        loginButton = new JButton("LOGIN");
        loginButton.setBounds(0, 0, 140, 45);
        loginButton.setBackground(new Color(76, 175, 80)); // Hijau
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createRaisedBevelBorder());
        loginButton.addActionListener(e -> handleLogin());
        panel.add(loginButton);

        registerButton = new JButton("REGISTER");
        registerButton.setBounds(160, 0, 140, 45);
        registerButton.setBackground(new Color(33, 150, 243)); // Biru
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setFocusPainted(false);
        registerButton.setBorder(BorderFactory.createRaisedBevelBorder());
        registerButton.addActionListener(e -> handleRegister());
        panel.add(registerButton);

        JButton exitButton = new JButton("EXIT");
        exitButton.setBounds(60, 60, 180, 35);
        exitButton.setBackground(new Color(244, 67, 54)); // Merah
        exitButton.setForeground(Color.WHITE);
        exitButton.setFont(new Font("Arial", Font.BOLD, 12));
        exitButton.setFocusPainted(false);
        exitButton.setBorder(BorderFactory.createRaisedBevelBorder());
        exitButton.addActionListener(e -> {
            stopBackgroundThreads();
            System.exit(0);
        });
        panel.add(exitButton);

        return panel;
    }

    private void loadBackgroundImages() {
        try {
            dayBackground = ImageIO.read(new File("assets/sprites/background-day.png"));
            nightBackground = ImageIO.read(new File("assets/sprites/background-night.png"));
        } catch (Exception e) {
            System.out.println("Warning: Background image not found. Using gradient fallback.");
        }
    }

    // --- LOGIKA FUNGSIONAL (Dari kode lama) ---

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("⚠️ Username dan password kosong!");
            statusLabel.setForeground(new Color(255, 100, 100));
            return;
        }

        try {
            // Menggunakan Class User yang sudah work dari kode lama
            User user = User.login(username, password);
            
            if (user != null) {
                statusLabel.setText("✅ Login berhasil!");
                statusLabel.setForeground(new Color(100, 255, 100));

                // Matikan animasi sebelum pindah frame agar hemat resource
                stopBackgroundThreads();

                // Pindah ke MainMenu
                SwingUtilities.invokeLater(() -> {
                    new MainMenu(user); // Membuka menu utama
                    this.dispose();     // Menutup login frame
                });

            } else {
                statusLabel.setText("❌ Username atau password salah!");
                statusLabel.setForeground(new Color(255, 100, 100));
            }
        } catch (Exception e) {
            statusLabel.setText("Error DB: " + e.getMessage());
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
            return;
        }

        if (password.length() < 3) {
            statusLabel.setText("Password minimal 3 karakter!");
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
                statusLabel.setText("✅ Registrasi berhasil! Silakan login.");
                statusLabel.setForeground(new Color(100, 255, 100));
                usernameField.setText("");
                passwordField.setText("");
            } else {
                statusLabel.setText("Registrasi gagal!");
                statusLabel.setForeground(new Color(255, 100, 100));
            }
        } catch (Exception e) {
            statusLabel.setText("Error DB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- THREAD ANIMASI ---

    private void startThreads() {
        running.set(true);

        // Thread 1: Geser Background (Parallax) ~60 FPS
        parallaxThread = new Thread(() -> {
            while (running.get()) {
                try {
                    Thread.sleep(16); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                bgOffsetX -= 1; // Kecepatan geser
                // Reset jika gambar sudah lewat sepenuhnya
                if (bgOffsetX <= -mainPanel.getWidth()) {
                    bgOffsetX = 0;
                }

                SwingUtilities.invokeLater(mainPanel::repaint);
            }
        }, "Parallax-Thread");

        // Thread 2: Ganti Siang/Malam setiap 10 detik
        dayNightThread = new Thread(() -> {
            while (running.get()) {
                try {
                    Thread.sleep(10000); // 10 Detik
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                isDay.set(!isDay.get()); // Toggle true/false
            }
        }, "DayNight-Thread");

        parallaxThread.start();
        dayNightThread.start();
    }

    private void stopBackgroundThreads() {
        running.set(false);
        if (parallaxThread != null) parallaxThread.interrupt();
        if (dayNightThread != null) dayNightThread.interrupt();
    }

    @Override
    public void dispose() {
        stopBackgroundThreads();
        super.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}