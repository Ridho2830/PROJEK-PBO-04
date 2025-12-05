import java.awt.*;

public abstract class GameObject {

    protected int x, y;
    protected int width, height;
    protected Image img;

    public GameObject(int x, int y, int width, int height, Image img) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.img = img;
    }

    public abstract void draw(Graphics g);

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
}