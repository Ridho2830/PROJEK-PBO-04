import java.awt.*;

public class Pipe {
    int x, y, width, height;
    Image img;
    boolean passed = false;

    Pipe(Image img) {
        this.img = img;
        this.x = 360;
        this.width = 64;
        this.height = 512;
    }

    void draw(Graphics g) {
        g.drawImage(img, x, y, width, height, null);
    }

    Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
