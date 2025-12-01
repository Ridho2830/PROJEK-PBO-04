import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    Bird bird;
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipeTimer;
    boolean gameOver = false;
    boolean gameStarted = false;
    double score = 0;

    private Image createColoredImage(int width, int height, Color color) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(color);
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();
        return img;
    }

    public FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        try {
            backgroundImg = new ImageIcon("assets/sprites/background-day.png").getImage();
            birdImg = new ImageIcon("assets/sprites/yellowbird-midflap.png").getImage();
            topPipeImg = new ImageIcon("assets/sprites/pipe-green.png").getImage();
            bottomPipeImg = new ImageIcon("assets/sprites/pipe-green.png").getImage();
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
            // Create colored rectangles as fallback
            backgroundImg = createColoredImage(boardWidth, boardHeight, Color.CYAN);
            birdImg = createColoredImage(34, 24, Color.YELLOW);
            topPipeImg = createColoredImage(64, 512, Color.GREEN);
            bottomPipeImg = createColoredImage(64, 512, Color.GREEN);
        }

        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        placePipeTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });

        gameLoop = new Timer(1000/60, this);
        gameLoop.start();
    }

    void placePipes() {
        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeight/4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, this.boardWidth, this.boardHeight, null);
        bird.draw(g);

        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.draw(g);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf((int) score), 10, 30);
            g.setFont(new Font("Arial", Font.PLAIN, 14));
            g.drawString("Press SPACE to restart", 10, 50);
        }
        else if (!gameStarted) {
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("FLAPPY BIRD", boardWidth/2 - 80, boardHeight/2 - 50);
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            g.drawString("Press SPACE to start", boardWidth/2 - 70, boardHeight/2);
        }
        else {
            g.drawString("Score: " + String.valueOf((int) score), 10, 30);
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
            }

            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }

        if (bird.y > boardHeight) {
            gameOver = true;
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
                // Restart game
                bird.y = 250;
                bird.velocityY = 0;
                pipes.clear();
                gameOver = false;
                gameStarted = false;
                score = 0;
                placePipeTimer.stop();
            } else if (!gameStarted) {
                gameStarted = true;
                placePipeTimer.start();
                bird.jump();
            } else {
                bird.jump();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
