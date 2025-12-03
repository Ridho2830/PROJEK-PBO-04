import java.awt.*;
import java.awt.geom.AffineTransform; // Import baru untuk rotasi

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
        // PERUBAHAN 1: Lompatan dibuat lebih ringan (dari -6 jadi -8)
        // Semakin negatif angkanya, semakin tinggi/ringan lompatnya.
        velocityY = -8; 
    }

    void draw(Graphics g) {
        // PERUBAHAN 2: Logika Rotasi (Menukik)
        Graphics2D g2d = (Graphics2D) g;
        
        // Simpan posisi grafik asli agar gambar lain (pipa/tanah) tidak ikut miring
        AffineTransform old = g2d.getTransform();

        // Pindahkan titik putar ke tengah burung
        g2d.translate(x + width/2, y + height/2);

        // Hitung sudut rotasi berdasarkan kecepatan (velocityY)
        // velocityY * 4 artinya: semakin cepat jatuh, semakin menukik
        // max -25 derajat (mendongak pas lompat)
        // min 90 derajat (nunduk pas jatuh)
        double rotation = Math.toRadians(Math.max(-25, Math.min(90, velocityY * 4)));
        
        g2d.rotate(rotation);

        // Gambar burung (koordinat digeser negatif setengah ukuran agar pas di tengah)
        g2d.drawImage(img, -width/2, -height/2, width, height, null);

        // Kembalikan posisi grafik ke semula
        g2d.setTransform(old);
    }

    Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}