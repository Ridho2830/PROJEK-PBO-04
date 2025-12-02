import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LeaderboardFrame extends JFrame {
    private Image backgroundImg;
    private User currentUser;

    public LeaderboardFrame(User user) {
        this.currentUser = user;
        setTitle("Flappy Bird - Leaderboard");
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

        JLabel titleLabel = new JLabel("TOP PLAYERS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(30, 80, 300, 40);
        mainPanel.add(titleLabel);

        JTextArea leaderboardArea = new JTextArea();
        leaderboardArea.setEditable(false);
        leaderboardArea.setFont(new Font("Monospaced", Font.BOLD, 16));
        leaderboardArea.setBackground(new Color(255, 255, 255, 200));
        leaderboardArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT username, high_score FROM users ORDER BY high_score DESC LIMIT 10";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            StringBuilder sb = new StringBuilder();
            int rank = 1;
            while (rs.next()) {
                sb.append(String.format("%2d. %-12s %d\n", 
                    rank++, rs.getString("username"), rs.getInt("high_score")));
            }
            if (sb.length() == 0) {
                sb.append("No scores yet!");
            }
            leaderboardArea.setText(sb.toString());
        } catch (SQLException e) {
            leaderboardArea.setText("Error loading leaderboard");
        }
        
        JScrollPane scrollPane = new JScrollPane(leaderboardArea);
        scrollPane.setBounds(30, 140, 300, 350);
        scrollPane.setBorder(BorderFactory.createRaisedBevelBorder());
        mainPanel.add(scrollPane);

        JButton backButton = new JButton("BACK TO MENU");
        backButton.setBounds(80, 520, 200, 50);
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setBackground(new Color(76, 175, 80));
        backButton.setForeground(Color.WHITE);
        backButton.setBorder(BorderFactory.createRaisedBevelBorder());
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> {
            new MainMenu(currentUser);
            this.dispose();
        });
        mainPanel.add(backButton);

        add(mainPanel);
        setVisible(true);
    }
}
