package classes;

public class PixelXY {
    private int x;
    private int y;

    public PixelXY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "PixelXY{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
