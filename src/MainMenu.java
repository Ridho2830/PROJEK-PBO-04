import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MainMenu extends JFrame {
    private Image backgroundImg;
    private User currentUser;

    public MainMenu(User user) {
        this.currentUser = user;
        setTitle("Flappy Bird - Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(360, 640);
        setLocationRelativeTo(null);
        setResizable(false);

        try {
            String bgFile = currentUser.getBackgroundMode().equals("day") ? 
                "background-day.png" : "background-night.png";
            backgroundImg = new ImageIcon("assets/sprites/" + bgFile).getImage();
        } catch (Exception e) {
            backgroundImg = null;
        }

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImg != null) {
                    g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), null);
                } else {
                    g.setColor(new Color(135, 206, 250));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        mainPanel.setLayout(null);

        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername() + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBounds(30, 100, 300, 30);
        mainPanel.add(welcomeLabel);

        JLabel highScoreLabel = new JLabel("Your Best: " + currentUser.getHighScore(), SwingConstants.CENTER);
        highScoreLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        highScoreLabel.setForeground(Color.YELLOW);
        highScoreLabel.setBounds(30, 130, 300, 25);
        mainPanel.add(highScoreLabel);

        // Bird Color Selector
        JLabel birdLabel = new JLabel("Bird Color:", SwingConstants.CENTER);
        birdLabel.setFont(new Font("Arial", Font.BOLD, 14));
        birdLabel.setForeground(Color.WHITE);
        birdLabel.setBounds(30, 160, 100, 25);
        mainPanel.add(birdLabel);

        JButton yellowBtn = createBirdButton(140, 160, "yellow");
        JButton redBtn = createBirdButton(180, 160, "red");
        JButton blueBtn = createBirdButton(220, 160, "blue");
        
        mainPanel.add(yellowBtn);
        mainPanel.add(redBtn);
        mainPanel.add(blueBtn);

        // Background Mode Toggle
        JButton bgToggle = new JButton(currentUser.getBackgroundMode().equals("day") ? "DAY MODE" : "NIGHT MODE");
        bgToggle.setBounds(80, 190, 200, 30);
        bgToggle.setFont(new Font("Arial", Font.BOLD, 12));
        bgToggle.setBackground(currentUser.getBackgroundMode().equals("day") ? 
            new Color(255, 215, 0) : new Color(25, 25, 112));
        bgToggle.setForeground(Color.WHITE);
        bgToggle.setBorder(BorderFactory.createRaisedBevelBorder());
        bgToggle.setFocusPainted(false);
        bgToggle.addActionListener(e -> {
            String newMode = currentUser.getBackgroundMode().equals("day") ? "night" : "day";
            currentUser.updateBackgroundMode(newMode);
            new MainMenu(currentUser);
            this.dispose();
        });
        mainPanel.add(bgToggle);

        JButton playButton = createMenuButton("PLAY", 240);
        playButton.addActionListener(e -> startGame());
        mainPanel.add(playButton);

        JButton leaderboardButton = createMenuButton("LEADERBOARD", 310);
        leaderboardButton.addActionListener(e -> showLeaderboard());
        mainPanel.add(leaderboardButton);

        JButton exitButton = createMenuButton("EXIT", 380);
        exitButton.addActionListener(e -> System.exit(0));
        mainPanel.add(exitButton);

        add(mainPanel);
        setVisible(true);
    }

    private JButton createBirdButton(int x, int y, String colorName) {
        JButton button = new JButton();
        button.setBounds(x, y, 60, 30);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setFocusPainted(false);
        
        try {
            String imagePath = "assets/sprites/" + colorName + "bird-midflap.png";
            ImageIcon icon = new ImageIcon(imagePath);
            Image img = icon.getImage().getScaledInstance(50, 25, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            button.setText(colorName.toUpperCase());
        }
        
        // Highlight current selection
        if (currentUser.getBirdColor().equals(colorName)) {
            button.setBorder(BorderFactory.createLoweredBevelBorder());
        }
        
        button.addActionListener(e -> {
            currentUser.updateBirdColor(colorName);
            // Refresh menu to show selection
            new MainMenu(currentUser);
            this.dispose();
        });
        
        return button;
    }

    private JButton createMenuButton(String text, int y) {
        JButton button = new JButton(text);
        button.setBounds(80, y, 200, 50);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(76, 175, 80));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setFocusPainted(false);
        return button;
    }

    private void startGame() {
        JFrame gameFrame = new JFrame("Flappy Bird");
        gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
        new LeaderboardFrame(currentUser);
        this.dispose();
    }
}
