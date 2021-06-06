package classes;

import javafx.scene.image.Image;

public class CanvasHistory {
    private boolean dimensionsChanged;
    private int width, height;
    private Image image;

    public CanvasHistory(Image image) {
        this.image = image;
        dimensionsChanged = false;
    }

    public CanvasHistory(int width, int height, Image image) {
        this.width = width;
        this.height = height;
        this.image = image;
        dimensionsChanged = true;
    }

    public boolean DimensionsChanged() {
        return dimensionsChanged;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Image getImage() {
        return image;
    }

    @Override
    public String toString() {
        return "CanvasHistory{" +
                "dimensionsChanged=" + dimensionsChanged +
                ", width=" + width +
                ", height=" + height +
                ", image=" + image +
                '}';
    }
}
