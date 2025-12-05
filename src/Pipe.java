import java.awt.*;
import java.awt.geom.AffineTransform;

// Pipe SEKARANG ADALAH ANAK DARI GameObject
public class Pipe extends GameObject {
    
    // Atribut khusus Pipe (yang tidak ada di GameObject)
    boolean passed = false;
    boolean isTopPipe;

    // Constructor
    public Pipe(int x, int y, int width, int height, Image img, boolean isTopPipe) {
        // Oper data ke constructor induk (GameObject)
        super(x, y, width, height, img); 
        this.isTopPipe = isTopPipe;
    }

    // Implementasi method abstract 'draw' dari GameObject
    @Override
    public void draw(Graphics g) {
        if (isTopPipe) {
            // Logika gambar pipa atas (diputar balik)
            Graphics2D g2d = (Graphics2D) g;
            AffineTransform old = g2d.getTransform();
            g2d.translate(x + width/2, y + height/2);
            g2d.rotate(Math.PI);
            g2d.drawImage(img, -width/2, -height/2, width, height, null);
            g2d.setTransform(old);
        } else {
            // Logika gambar pipa bawah (normal)
            g.drawImage(img, x, y, width, height, null);
        }
    }
}