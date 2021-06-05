package classes;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class ImageFxIO {
	Stage stage;
	public ImageFxIO(Stage stage) {
		this.stage = stage;
	}
	
	public File saveToFile(Image image, String format, File f) {
		try {
			WritableImage writableImage = new WritableImage(image.getPixelReader(), (int)image.getWidth(), (int)image.getHeight());
			BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);
	        BufferedImage imageRGB = new BufferedImage(
	        bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.OPAQUE);
	        Graphics2D graphics = imageRGB.createGraphics();
	        graphics.drawImage(bufferedImage, 0, 0, null);
	        ImageIO.write(imageRGB, format, f);
			return f;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	

	public File saveToFile(Image image) {
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG", "*.png")
				,new FileChooser.ExtensionFilter("JPG", "*.jpg", "*.jpeg", "*.jpe", "*.jfif")
				);
		File file = fc.showSaveDialog(stage);
		if (file != null) {
			return saveToFile(image, fc.getSelectedExtensionFilter().getDescription(), file);			
		}
		return null;
	}
	public Object[] oepnFromFile() {
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(
				"Image", "*.png","*.jpg", "*.jpeg", "*.jpe", "*.jfif"),
				new FileChooser.ExtensionFilter("All","*.*")
				);
		File file = fc.showOpenDialog(stage);
		if (file != null) {
			BufferedImage bufferedImage;
			Image image;
			try {
				bufferedImage = ImageIO.read(file);
				image = SwingFXUtils.toFXImage(bufferedImage, null);
			} catch (Exception e) {return null;}
			Object[] result = {image, file};
			return result;
		}
		return null;
	}
}