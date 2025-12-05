import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import javax.sound.sampled.*;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    // Aset Gambar
    Image imgDay, imgNight; // Aset background siang & malam disimpan di memori
    Image currentBgImg;     // Background yang sedang aktif
    Image[] birdImgs = new Image[3];
    Image[] numberImgs = new Image[10];
    Image topPipeImg;
    Image bottomPipeImg;
    Image baseImg;
    Image gameOverImg;
    Image currentBirdImg;

    // Game Logic Variables
    int birdX = boardWidth / 8;
    double birdY = boardHeight / 2;
    double velocityY = 0;
    double gravity = 0.4;
    double jumpStrength = -6;

    int birdWidth = 34;
    int birdHeight = 24;

    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    // --- LOGIKA BACKGROUND BERGERAK ---
    int backgroundX = 0;    // Posisi X background
    int bgSpeed = 1;        // Kecepatan gerak background (lebih lambat dari pipa biar estetik)
    // ----------------------------------

    // --- LOGIKA KECEPATAN BERTINGKAT ---
    int baseSpeed = 3;      
    int currentSpeed = 3;   
    // -----------------------------------

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipeTimer;
    Timer animationTimer;

    boolean gameOver = false;
    boolean gameStarted = false;
    double score = 0;

    User currentUser;

    // === Background Music Clip ===
    Clip bgmClip;

    public FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        currentUser = User.getCurrentUser();

        loadAssets();

        birdY = boardHeight / 2;
        currentBirdImg = birdImgs[1];

        pipes = new ArrayList<>();

        placePipeTimer = new Timer(1500, e -> placePipes());

        animationTimer = new Timer(150, new ActionListener() {
            int frame = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameStarted && !gameOver) {
                    frame++;
                    currentBirdImg = birdImgs[frame % 3];
                }
            }
        });

        gameLoop = new Timer(1000/60, this);
        gameLoop.start();
        animationTimer.start();
    }

    private void loadAssets() {
        try {
            // Load KEDUA background (Siang dan Malam)
            imgDay = new ImageIcon("assets/sprites/background-day.png").getImage();
            imgNight = new ImageIcon("assets/sprites/background-night.png").getImage();

            // Set background awal sesuai preferensi user
            String initialMode = currentUser != null ? currentUser.getBackgroundMode() : "day";
            currentBgImg = initialMode.equals("day") ? imgDay : imgNight;

            String birdColor = currentUser != null ? currentUser.getBirdColor() : "yellow";
            birdImgs[0] = new ImageIcon("assets/sprites/" + birdColor + "bird-upflap.png").getImage();
            birdImgs[1] = new ImageIcon("assets/sprites/" + birdColor + "bird-midflap.png").getImage();
            birdImgs[2] = new ImageIcon("assets/sprites/" + birdColor + "bird-downflap.png").getImage();

            topPipeImg = new ImageIcon("assets/sprites/pipe-green.png").getImage();
            bottomPipeImg = new ImageIcon("assets/sprites/pipe-green.png").getImage();
            baseImg = new ImageIcon("assets/sprites/base.png").getImage();
            gameOverImg = new ImageIcon("assets/sprites/gameover.png").getImage();

            for (int i = 0; i < 10; i++) {
                numberImgs[i] = new ImageIcon("assets/sprites/" + i + ".png").getImage();
            }
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
        }
    }

    private void playBackgroundMusic(String filename) {
        try {
            File file = new File("assets/audio/" + filename);
            if (!file.exists()) return;

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            bgmClip = AudioSystem.getClip();
            bgmClip.open(audioStream);
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY); 
            bgmClip.start();
        } catch (Exception e) {
            System.out.println("BGM error: " + e.getMessage());
        }
    }

    private void stopBackgroundMusic() {
        try {
            if (bgmClip != null) {
                bgmClip.stop();
                bgmClip.close();
            }
        } catch (Exception e) {}
    }

    void placePipes() {
        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeight/4;

        Pipe topPipe = new Pipe(boardWidth, randomPipeY, pipeWidth, pipeHeight, topPipeImg, true);
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(boardWidth, topPipe.getY() + pipeHeight + openingSpace, pipeWidth, pipeHeight, bottomPipeImg, false);
        pipes.add(bottomPipe);

        playSound("swoosh.wav");
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // 1. Background Bergerak (Draw 2 image untuk looping)
        // Gambar 1 (Posisi saat ini)
        g.drawImage(currentBgImg, backgroundX, 0, boardWidth, boardHeight, null);
        // Gambar 2 (Posisi setelah gambar 1 habis)
        g.drawImage(currentBgImg, backgroundX + boardWidth, 0, boardWidth, boardHeight, null);

        // 2. Burung (dengan Rotasi)
        Graphics2D g2d = (Graphics2D) g;
        var old = g2d.getTransform();
        g2d.translate(birdX + birdWidth/2, birdY + birdHeight/2);
        double rotation = Math.toRadians(Math.max(-25, Math.min(90, velocityY * 5)));
        if (!gameStarted) rotation = 0;
        g2d.rotate(rotation);
        g2d.drawImage(currentBirdImg, -birdWidth/2, -birdHeight/2, birdWidth, birdHeight, null);
        g2d.setTransform(old);

        // 3. Pipa
        for (Pipe pipe : pipes) {
            pipe.draw(g);
        }

        // 4. Tanah (Base)
        g.drawImage(baseImg, 0, boardHeight - 112, boardWidth, 112, null);

        // === LOGIKA TAMPILAN ===
        if (gameOver) {
            drawGameOverScreen(g);
        }
        else if (!gameStarted) {
            drawGetReadyScreen(g);
        }
        else {
            drawScoreCentered(g, (int) score, 80);
        }
    }

    private void drawGetReadyScreen(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.setColor(Color.BLACK);
        g.drawString("GET READY!", boardWidth/2 - 82, boardHeight/2 - 48);
        g.setColor(Color.WHITE);
        g.drawString("GET READY!", boardWidth/2 - 80, boardHeight/2 - 50);

        g.setFont(new Font("Arial", Font.PLAIN, 16));
        drawCenteredString(g, "Press SPACE to start", boardWidth/2, boardHeight/2);
    }

    private void drawGameOverScreen(Graphics g) {
        g.drawImage(gameOverImg, boardWidth/2 - 96, boardHeight/2 - 180, null);

        int boardW = 226;
        int boardH = 114;
        int boardX = boardWidth/2 - (boardW/2);
        int boardY = boardHeight/2 - 90;

        g.setColor(new Color(222, 216, 149));
        g.fillRoundRect(boardX, boardY, boardW, boardH, 10, 10);

        Graphics2D g2dBoard = (Graphics2D) g;
        g2dBoard.setColor(new Color(84, 56, 71));
        g2dBoard.setStroke(new BasicStroke(3));
        g2dBoard.drawRoundRect(boardX, boardY, boardW, boardH, 10, 10);

        g.setColor(new Color(232, 97, 1)); 
        g.setFont(new Font("Arial", Font.BOLD, 16)); 

        int labelX = boardX + 20; 
        int valueX = boardX + boardW - 25; 

        g.drawString("SCORE", labelX, boardY + 40);
        g.drawString("BEST", labelX, boardY + 85);

        drawScoreAt(g, (int)score, valueX, boardY + 20);

        int best = currentUser != null ? currentUser.getHighScore() : 0;
        drawScoreAt(g, best, valueX, boardY + 65);

        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.setColor(Color.BLACK);
        drawCenteredString(g, "Press SPACE to Restart", boardWidth/2 + 1, boardHeight/2 + 61);
        drawCenteredString(g, "Press M for Menu", boardWidth/2 + 1, boardHeight/2 + 86);

        g.setColor(Color.WHITE);
        drawCenteredString(g, "Press SPACE to Restart", boardWidth/2, boardHeight/2 + 60);
        drawCenteredString(g, "Press M for Menu", boardWidth/2, boardHeight/2 + 85);

        if (currentUser != null) {
            currentUser.updateHighScore((int) score);
        }
    }

    private void drawScoreCentered(Graphics g, int score, int y) {
        String scoreStr = String.valueOf(score);
        int digitWidth = 24;
        int totalWidth = scoreStr.length() * digitWidth;
        int startX = boardWidth / 2 - totalWidth / 2;

        for (int i = 0; i < scoreStr.length(); i++) {
            int digit = Character.getNumericValue(scoreStr.charAt(i));
            if (numberImgs[digit] != null) {
                g.drawImage(numberImgs[digit], startX + i * digitWidth, y, null);
            }
        }
    }

    private void drawScoreAt(Graphics g, int score, int endX, int y) {
        String scoreStr = String.valueOf(score);
        int digitWidth = 16; 
        int digitHeight = 24; 

        for (int i = scoreStr.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(scoreStr.charAt(i));
            int x = endX - ((scoreStr.length() - i) * digitWidth);
            if (numberImgs[digit] != null) {
                g.drawImage(numberImgs[digit], x, y, digitWidth, digitHeight, null);
            }
        }
    }

    private void drawCenteredString(Graphics g, String text, int x, int y) {
        FontMetrics metrics = g.getFontMetrics(g.getFont());
        int xCentered = x - metrics.stringWidth(text) / 2;
        g.drawString(text, xCentered, y);
    }

    public void move() {
        if (!gameStarted) return;

        // === 1. UPDATE KECEPATAN (Progressive Difficulty) ===
        currentSpeed = baseSpeed + (int)(score / 20);
        currentSpeed = Math.min(10, currentSpeed);

        // === 2. UPDATE BACKGROUND (Bergerak & Berubah) ===
        // Geser background ke kiri
        backgroundX -= bgSpeed; 
        // Jika gambar pertama sudah lewat sepenuhnya, reset ke 0
        if (backgroundX <= -boardWidth) {
            backgroundX = 0;
        }

        // Ganti Background Siang/Malam setiap 30 Poin
        // Logika: 0-29 (Siang), 30-59 (Malam), 60-89 (Siang), dst.
        if (((int)score / 30) % 2 == 0) {
            currentBgImg = imgDay;   // Genap = Siang
        } else {
            currentBgImg = imgNight; // Ganjil = Malam
        }
        // =================================================

        // 3. Gravitasi Burung
        velocityY += gravity;
        birdY += velocityY;
        birdY = Math.max(birdY, 0);

        // 4. Loop Pipa
        for (Pipe pipe : pipes) {
            if (!gameOver) {
                pipe.x -= currentSpeed; 

                if (!pipe.passed && birdX > pipe.x + pipe.width) {
                    score += 0.5;
                    pipe.passed = true;
                    if (score % 1 == 0) playSound("point.wav");
                }

                if (collision(pipe)) {
                    triggerGameOver();
                }
            }
        }

        if (!gameOver && pipes.size() > 0 && pipes.get(0).x + pipeWidth < 0) {
            pipes.remove(0);
            pipes.remove(0);
        }

        if (birdY > boardHeight - 112 - birdHeight) {
            triggerGameOver();
        }
    }

    boolean collision(Pipe p) {
        Rectangle birdRect = new Rectangle(birdX, (int)birdY, birdWidth, birdHeight);
        return birdRect.intersects(p.getBounds());
    }

    private void triggerGameOver() {
        if (gameOver) return;
        gameOver = true;

        stopBackgroundMusic(); 
        playSound("hit.wav");
        playSound("die.wav");
        placePipeTimer.stop();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (gameOver) {
                // RESTART GAME
                birdY = boardHeight / 2;
                velocityY = 0;
                pipes.clear();
                gameOver = false;
                gameStarted = false;
                score = 0;
                
                // Reset Logika Tambahan
                currentSpeed = baseSpeed;
                backgroundX = 0; 
                // Kembalikan background ke mode user awal
                String mode = currentUser != null ? currentUser.getBackgroundMode() : "day";
                currentBgImg = mode.equals("day") ? imgDay : imgNight;

                placePipeTimer.stop();
                stopBackgroundMusic();
                currentBirdImg = birdImgs[1];
            }
            else if (!gameStarted) {
                gameStarted = true;
                placePipeTimer.start();
                stopBackgroundMusic();
                playBackgroundMusic("music.wav");

                birdY += jumpStrength;
                velocityY = jumpStrength;
                playSound("wing.wav");
            }
            else {
                velocityY = jumpStrength;
                playSound("wing.wav");
            }
        }
        else if (e.getKeyCode() == KeyEvent.VK_M && gameOver) {
            stopBackgroundMusic();
            if (currentUser != null) {
                new MainMenu(currentUser);
                JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
                if (parent != null) parent.dispose();
            }
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    private void playSound(String soundFile) {
        try {
            File file = new File("assets/audio/" + soundFile);
            if (file.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
            }
        } catch (Exception e) {}
    }
}