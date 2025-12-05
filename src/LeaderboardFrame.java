import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.*;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.imageio.ImageIO;
import javax.swing.*;

public class LeaderboardFrame extends JFrame {
    
    private User currentUser;
    
    // Variabel Animasi Background
    private BufferedImage dayBackground;
    private BufferedImage nightBackground;
    private int bgOffsetX = 0;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private Thread parallaxThread;
    private JPanel mainPanel;

    public LeaderboardFrame(User user) {
        this.currentUser = user;
        setTitle("Flappy Bird - Leaderboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(360, 640);
        setLocationRelativeTo(null);
        setResizable(false);

        // Load gambar
        loadBackgroundImages();

        // === MAIN PANEL (Background Bergerak) ===
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                
                // Cek mode user
                boolean isDay = currentUser.getBackgroundMode().equalsIgnoreCase("day");
                BufferedImage bg = isDay ? dayBackground : nightBackground;
                
                int w = getWidth();
                int h = getHeight();

                if (bg != null) {
                    // Gambar 3x untuk looping seamless
                    g2d.drawImage(bg, bgOffsetX, 0, w, h, null);
                    g2d.drawImage(bg, bgOffsetX + w, 0, w, h, null);
                    g2d.drawImage(bg, bgOffsetX - w, 0, w, h, null);
                } else {
                    g2d.setColor(isDay ? new Color(135, 206, 250) : new Color(0, 0, 50));
                    g2d.fillRect(0, 0, w, h);
                }
                g2d.dispose();
            }
        };
        mainPanel.setLayout(null);
        add(mainPanel);
        
        // === UI ELEMENTS ===

        // Judul dengan Shadow Effect
        JLabel titleLabel = new JLabel("TOP PLAYERS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial Black", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(30, 60, 300, 40);
        mainPanel.add(titleLabel);

        // Panel Pembungkus (Efek Kaca/Glass)
        JPanel glassPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Kotak Hitam Transparan
                g2d.setColor(new Color(0, 0, 0, 100)); 
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Border Putih Tipis
                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
            }
        };
        glassPanel.setOpaque(false);
        glassPanel.setLayout(new BorderLayout());
        glassPanel.setBounds(30, 120, 300, 380);
        
        // Area Text Leaderboard
        JTextArea leaderboardArea = new JTextArea();
        leaderboardArea.setEditable(false);
        // Font Monospaced penting agar kolom sejajar
        leaderboardArea.setFont(new Font("Monospaced", Font.BOLD, 14)); 
        leaderboardArea.setForeground(Color.WHITE);
        leaderboardArea.setOpaque(false); // Transparan agar glassPanel terlihat
        leaderboardArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Load Data dari Database
        loadLeaderboardData(leaderboardArea);

        // Scroll Pane (Jaga-jaga kalau datanya banyak)
        JScrollPane scrollPane = new JScrollPane(leaderboardArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null); // Hilangkan border bawaan scrollpane

        glassPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(glassPanel);

        // Tombol Kembali
        JButton backButton = new JButton("BACK TO MENU");
        backButton.setBounds(80, 530, 200, 45);
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(new Color(244, 67, 54)); // Merah
        backButton.setForeground(Color.WHITE);
        backButton.setBorder(BorderFactory.createRaisedBevelBorder());
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> {
            stopParallax(); // Stop animasi sebelum pindah
            new MainMenu(currentUser);
            this.dispose();
        });
        mainPanel.add(backButton);

        // Mulai Animasi
        startParallax();
        setVisible(true);
    }

    private void loadLeaderboardData(JTextArea area) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT username, high_score FROM users ORDER BY high_score DESC LIMIT 10";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            StringBuilder sb = new StringBuilder();
            
            // Header Tabel
            sb.append(String.format("%-4s %-16s %s\n", "RK", "PLAYER", "SCORE"));
            sb.append("------------------------------\n");
            
            int rank = 1;
            boolean hasData = false;
            
            while (rs.next()) {
                hasData = true;
                String username = rs.getString("username");
                int score = rs.getInt("high_score");
                
                // Potong username jika terlalu panjang biar rapi
                if (username.length() > 14) {
                    username = username.substring(0, 11) + "...";
                }
                
                // Format kolom: Rank(4 spasi) Username(16 spasi) Score
                sb.append(String.format("%-4d %-16s %d\n", rank, username, score));
                rank++;
                
                // Tambah spasi antar baris biar gak rapat
                sb.append("\n"); 
            }
            
            if (!hasData) {
                sb.append("\n      No Data Yet!");
            }
            
            area.setText(sb.toString());
            
        } catch (SQLException e) {
            area.setText("Error loading data:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    // === BACKGROUND ANIMATION LOGIC (SAMA SEPERTI MAIN MENU) ===

    private void loadBackgroundImages() {
        try {
            dayBackground = ImageIO.read(new File("assets/sprites/background-day.png"));
            nightBackground = ImageIO.read(new File("assets/sprites/background-night.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startParallax() {
        running.set(true);
        parallaxThread = new Thread(() -> {
            while (running.get()) {
                try {
                    Thread.sleep(16); // ~60 FPS
                } catch (InterruptedException e) {
                    break;
                }
                bgOffsetX -= 1;
                if (bgOffsetX <= -mainPanel.getWidth()) {
                    bgOffsetX = 0;
                }
                SwingUtilities.invokeLater(() -> mainPanel.repaint());
            }
        });
        parallaxThread.start();
    }

    private void stopParallax() {
        running.set(false);
        if (parallaxThread != null) {
            try {
                parallaxThread.join(100); // Tunggu thread mati sebentar
            } catch (InterruptedException e) {}
        }
    }
}