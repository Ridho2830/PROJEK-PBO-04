import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import javax.sound.sampled.*;
import java.io.File;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    Image backgroundImg;
    Image[] birdImgs = new Image[3];
    Image[] numberImgs = new Image[10];
    Image topPipeImg;
    Image bottomPipeImg;
    Image baseImg;
    Image gameOverImg;

    Bird bird;
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipeTimer;
    Timer animationTimer;
    boolean gameOver = false;
    boolean gameStarted = false;
    boolean soundPlayed = false;
    double score = 0;

    private Image createColoredImage(int width, int height, Color color) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(color);
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();
        return img;
    }

    private void playSound(String soundFile) {
        try {
            File file = new File("assets/audio/" + soundFile);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });
            clip.start();
        } catch (Exception e) {
            // Silent fail for sound
        }
    }

    private void drawScore(Graphics g, int score, int x, int y) {
        String scoreStr = String.valueOf(score);
        int digitWidth = 24;
        int totalWidth = scoreStr.length() * digitWidth;
        int startX = x - totalWidth / 2;
        
        for (int i = 0; i < scoreStr.length(); i++) {
            int digit = Character.getNumericValue(scoreStr.charAt(i));
            g.drawImage(numberImgs[digit], startX + i * digitWidth, y, null);
        }
    }

    public FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        try {
            // Load background based on user preference
            User currentUser = User.getCurrentUser();
            String bgMode = currentUser != null ? currentUser.getBackgroundMode() : "day";
            String birdColor = currentUser != null ? currentUser.getBirdColor() : "yellow";
            
            backgroundImg = new ImageIcon("assets/sprites/background-" + bgMode + ".png").getImage();
            
            // Load bird sprites based on user preference
            birdImgs[0] = new ImageIcon("assets/sprites/" + birdColor + "bird-upflap.png").getImage();
            birdImgs[1] = new ImageIcon("assets/sprites/" + birdColor + "bird-midflap.png").getImage();
            birdImgs[2] = new ImageIcon("assets/sprites/" + birdColor + "bird-downflap.png").getImage();
            
            topPipeImg = new ImageIcon("assets/sprites/pipe-green.png").getImage();
            bottomPipeImg = new ImageIcon("assets/sprites/pipe-green.png").getImage();
            baseImg = new ImageIcon("assets/sprites/base.png").getImage();
            gameOverImg = new ImageIcon("assets/sprites/gameover.png").getImage();
            
            // Load number sprites
            for (int i = 0; i < 10; i++) {
                numberImgs[i] = new ImageIcon("assets/sprites/" + i + ".png").getImage();
            }
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
            backgroundImg = createColoredImage(boardWidth, boardHeight, Color.CYAN);
            birdImgs[0] = birdImgs[1] = birdImgs[2] = createColoredImage(34, 24, Color.YELLOW);
            topPipeImg = createColoredImage(64, 512, Color.GREEN);
            bottomPipeImg = createColoredImage(64, 512, Color.GREEN);
            baseImg = createColoredImage(boardWidth, 112, Color.GREEN);
            gameOverImg = createColoredImage(192, 42, Color.RED);
            
            // Fallback for numbers
            for (int i = 0; i < 10; i++) {
                numberImgs[i] = createColoredImage(24, 36, Color.WHITE);
            }
        }

        bird = new Bird(birdImgs[1]);
        pipes = new ArrayList<Pipe>();

        placePipeTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });

        animationTimer = new Timer(200, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameStarted && !gameOver) {
                    // Animasi berdasarkan arah gerakan burung
                    if (bird.velocityY < -2) {
                        bird.img = birdImgs[0]; // upflap saat naik cepat
                    } else if (bird.velocityY > 2) {
                        bird.img = birdImgs[2]; // downflap saat turun cepat
                    } else {
                        bird.img = birdImgs[1]; // midflap saat stabil
                    }
                }
            }
        });

        gameLoop = new Timer(1000/60, this);
        gameLoop.start();
        animationTimer.start();
    }

    void placePipes() {
        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeight/4;

        Pipe topPipe = new Pipe(topPipeImg, true);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg, false);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
        
        playSound("swoosh.wav");
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, this.boardWidth, this.boardHeight, null);
        
        // Draw base
        g.drawImage(baseImg, 0, boardHeight - 112, boardWidth, 112, null);
        
        bird.draw(g);

        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.draw(g);
        }

        if (gameOver) {
            // Game Over layout with proper spacing
            g.drawImage(gameOverImg, boardWidth/2 - 96, boardHeight/2 - 120, null);
            
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.drawString("Score: " + (int) score, boardWidth/2 - 40, boardHeight/2 - 60);
            
            User currentUser = User.getCurrentUser();
            if (currentUser != null) {
                currentUser.updateHighScore((int) score);
                g.drawString("Best: " + currentUser.getHighScore(), boardWidth/2 - 35, boardHeight/2 - 30);
            }
            
            g.setFont(new Font("Arial", Font.PLAIN, 14));
            g.drawString("Press SPACE to restart", boardWidth/2 - 70, boardHeight/2 + 10);
            g.drawString("Press M for Main Menu", boardWidth/2 - 65, boardHeight/2 + 30);
        }
        else if (!gameStarted) {
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("FLAPPY BIRD", boardWidth/2 - 80, boardHeight/2 - 50);
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            g.drawString("Press SPACE to start", boardWidth/2 - 70, boardHeight/2);
        }
        else {
            // Draw score with number sprites at top center
            drawScore(g, (int) score, boardWidth/2, 80);
        }
    }

    public void move() {
        if (!gameStarted) return;
        
        bird.update();

        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x -= 4;

            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                score += 0.5;
                pipe.passed = true;
                if (score % 1 == 0) {
                    playSound("point.wav");
                }
            }

            if (collision(bird, pipe)) {
                if (!gameOver) {
                    gameOver = true;
                    playSound("hit.wav");
                    playSound("die.wav");
                    soundPlayed = true;
                }
            }
        }

        if (bird.y > boardHeight - 112 - bird.height) {
            if (!gameOver) {
                gameOver = true;
                playSound("hit.wav");
                playSound("die.wav");
                soundPlayed = true;
            }
        }
    }

    boolean collision(Bird a, Pipe b) {
        return a.getBounds().intersects(b.getBounds());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipeTimer.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (gameOver) {
                bird.y = 250;
                bird.velocityY = 0;
                pipes.clear();
                gameOver = false;
                gameStarted = false;
                soundPlayed = false;
                score = 0;
                placePipeTimer.stop();
                bird.img = birdImgs[1];
            } else if (!gameStarted) {
                gameStarted = true;
                placePipeTimer.start();
                bird.jump();
                playSound("wing.wav");
            } else {
                bird.jump();
                playSound("wing.wav");
            }
        } else if (e.getKeyCode() == KeyEvent.VK_M && gameOver) {
            User currentUser = User.getCurrentUser();
            if (currentUser != null) {
                new MainMenu(currentUser);
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                if (parentFrame != null) {
                    parentFrame.dispose();
                }
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
