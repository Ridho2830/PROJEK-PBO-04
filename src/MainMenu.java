import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.imageio.ImageIO;
import javax.swing.*;

public class MainMenu extends JFrame {
    
    private User currentUser;
    
    // Variabel Background
    private BufferedImage dayBackground;
    private BufferedImage nightBackground;
    private int bgOffsetX = 0;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private Thread parallaxThread;
    private JPanel mainPanel;

    // Audio Player
    private static MusicPlayer music = new MusicPlayer();

    // List tombol burung
    private List<JButton> birdButtons = new ArrayList<>(); 

    public MainMenu(User user) {
        this.currentUser = user;
        setTitle("Flappy Bird - Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(360, 640);
        setLocationRelativeTo(null);
        setResizable(false);

        loadBackgroundImages();

        // === MAIN PANEL ===
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                
                boolean isDay = currentUser.getBackgroundMode().equalsIgnoreCase("day");
                BufferedImage bg = isDay ? dayBackground : nightBackground;
                
                int w = getWidth();
                int h = getHeight();

                if (bg != null) {
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
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername() + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial Black", Font.BOLD, 22));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBounds(28, 102, 300, 30);
        mainPanel.add(welcomeLabel);
        
        JLabel welcomeLabelFront = new JLabel("Welcome, " + currentUser.getUsername() + "!", SwingConstants.CENTER);
        welcomeLabelFront.setFont(new Font("Arial Black", Font.BOLD, 22));
        welcomeLabelFront.setForeground(Color.WHITE);
        welcomeLabelFront.setBounds(30, 100, 300, 30);
        mainPanel.add(welcomeLabelFront);

        JLabel highScoreLabel = new JLabel("Best Score: " + currentUser.getHighScore(), SwingConstants.CENTER);
        highScoreLabel.setFont(new Font("Arial", Font.BOLD, 18));
        highScoreLabel.setForeground(new Color(255, 215, 0));
        highScoreLabel.setBounds(30, 135, 300, 25);
        mainPanel.add(highScoreLabel);

        JLabel birdLabel = new JLabel("Choose Bird:", SwingConstants.CENTER);
        birdLabel.setFont(new Font("Arial", Font.BOLD, 14));
        birdLabel.setForeground(Color.WHITE);
        birdLabel.setBounds(30, 175, 100, 25);
        mainPanel.add(birdLabel);

        // --- BIRD BUTTONS ---
        int startX = 140;
        int gap = 50;
        
        mainPanel.add(createBirdButton(startX, 165, "yellow"));
        mainPanel.add(createBirdButton(startX + gap, 165, "red"));
        mainPanel.add(createBirdButton(startX + (gap * 2), 165, "blue"));
        
        updateBirdBorders(); 

        // --- TOGGLE BACKGROUND ---
        boolean isDayStart = currentUser.getBackgroundMode().equalsIgnoreCase("day");
        JButton bgToggle = new JButton(isDayStart ? "SWITCH TO NIGHT" : "SWITCH TO DAY");
        bgToggle.setBounds(80, 220, 200, 35);
        bgToggle.setFont(new Font("Arial", Font.BOLD, 12));
        bgToggle.setBackground(isDayStart ? new Color(255, 165, 0) : new Color(75, 0, 130));
        bgToggle.setForeground(Color.WHITE);
        bgToggle.setBorder(BorderFactory.createRaisedBevelBorder());
        bgToggle.setFocusPainted(false);
        
        bgToggle.addActionListener(e -> {
            boolean currentIsDay = currentUser.getBackgroundMode().equalsIgnoreCase("day");
            String newMode = currentIsDay ? "night" : "day";
            
            currentUser.updateBackgroundMode(newMode);
            
            boolean nowDay = newMode.equals("day");
            bgToggle.setText(nowDay ? "SWITCH TO NIGHT" : "SWITCH TO DAY");
            bgToggle.setBackground(nowDay ? new Color(255, 165, 0) : new Color(75, 0, 130));
            
            mainPanel.repaint();
        });
        mainPanel.add(bgToggle);

        // --- MENU BUTTONS ---
        JButton playButton = createMenuButton("PLAY GAME", 280, new Color(76, 175, 80));
        playButton.addActionListener(e -> startGame());
        mainPanel.add(playButton);

        JButton leaderboardButton = createMenuButton("LEADERBOARD", 340, new Color(33, 150, 243));
        leaderboardButton.addActionListener(e -> showLeaderboard());
        mainPanel.add(leaderboardButton);

        // === BAGIAN INI YANG DIUBAH (LOGOUT) ===
        JButton exitButton = createMenuButton("LOGOUT", 400, new Color(244, 67, 54));
        exitButton.addActionListener(e -> {
            stopParallax();       // 1. Matikan Thread
            new LoginFrame();     // 2. Buka LoginFrame
            this.dispose();       // 3. Tutup Menu
        });
        mainPanel.add(exitButton);

        music.play("assets/audio/menu-music.wav", true);
        startParallax();
        setVisible(true);
    }

    // --- HELPER METHODS ---
    
    private JButton createBirdButton(int x, int y, String colorName) {
        JButton button = new JButton();
        button.setBounds(x, y, 45, 45);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false); 
        button.putClientProperty("birdColor", colorName);

        try {
            String imagePath = "assets/sprites/" + colorName + "bird-midflap.png";
            BufferedImage rawImg = ImageIO.read(new File(imagePath));
            Image scaledImg = rawImg.getScaledInstance(34, 24, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(scaledImg));
        } catch (Exception e) {
            button.setText("?");
        }

        button.addActionListener(e -> {
            currentUser.updateBirdColor(colorName);
            updateBirdBorders();
        });

        birdButtons.add(button); 
        return button;
    }

    private void updateBirdBorders() {
        String selected = currentUser.getBirdColor();
        for (JButton btn : birdButtons) {
            String btnColor = (String) btn.getClientProperty("birdColor");
            if (selected.equalsIgnoreCase(btnColor)) {
                btn.setBorderPainted(true);
                btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
            } else {
                btn.setBorderPainted(false);
            }
        }
        mainPanel.repaint();
    }

    private JButton createMenuButton(String text, int y, Color color) {
        JButton button = new JButton(text);
        button.setBounds(60, y, 240, 45);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setFocusPainted(false);
        return button;
    }

    private void loadBackgroundImages() {
        try {
            dayBackground = ImageIO.read(new File("assets/sprites/background-day.png"));
            nightBackground = ImageIO.read(new File("assets/sprites/background-night.png"));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void startParallax() {
        running.set(true);
        parallaxThread = new Thread(() -> {
            while (running.get()) {
                try { Thread.sleep(16); } catch (InterruptedException e) { break; }
                bgOffsetX -= 1;
                if (bgOffsetX <= -mainPanel.getWidth()) bgOffsetX = 0;
                SwingUtilities.invokeLater(() -> mainPanel.repaint());
            }
        });
        parallaxThread.start();
    }

    private void stopParallax() {
        running.set(false);
    }

    private void startGame() {
        stopParallax();
        music.stop();
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
        
        this.dispose();
    }

    private void showLeaderboard() {
        stopParallax();
        new LeaderboardFrame(currentUser);
        this.dispose();
    }
}