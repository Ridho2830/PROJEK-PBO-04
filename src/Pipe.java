import java.awt.*;
import java.awt.geom.AffineTransform;

public class Pipe {
    int x, y, width, height;
    Image img;
    boolean passed = false;
    boolean isTopPipe = false;

    Pipe(Image img) {
        this.img = img;
        this.x = 360;
        this.width = 64;
        this.height = 512;
    }

    Pipe(Image img, boolean isTopPipe) {
        this(img);
        this.isTopPipe = isTopPipe;
    }

    void draw(Graphics g) {
        if (isTopPipe) {
            Graphics2D g2d = (Graphics2D) g;
            AffineTransform old = g2d.getTransform();
            g2d.translate(x + width/2, y + height/2);
            g2d.rotate(Math.PI);
            g2d.drawImage(img, -width/2, -height/2, width, height, null);
            g2d.setTransform(old);
        } else {
            g.drawImage(img, x, y, width, height, null);
        }
    }

    Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
