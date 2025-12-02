import java.awt.*;

public class Bird {
    int x, y, width, height;
    Image img;
    int velocityY = 0;
    int gravity = 1;

    Bird(Image img) {
        this.img = img;
        this.x = 60;
        this.y = 250;
        this.width = 34;
        this.height = 24;
    }

    void update() {
        velocityY += gravity;
        y += velocityY;
        y = Math.max(y, 0);
    }

    void jump() {
        velocityY = -6;
    }

    void draw(Graphics g) {
        g.drawImage(img, x, y, width, height, null);
    }

    Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
