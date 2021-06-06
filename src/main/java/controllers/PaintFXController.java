package controllers;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

import classes.CanvasHistory;
import classes.ImageFxIO;
import classes.PixelXY;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Slider;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class PaintFXController {

	@FXML
	ImageView lockUnlock;
	@FXML
	Slider ScaleSlider;
	@FXML
	Label pixelXY;
	@FXML
	Label projectName;
	@FXML
	Label canvasWidthHeight;
	@FXML
	AnchorPane CanvasAnchor;
	@FXML
	Canvas canvas;
	@FXML
	AnchorPane BigAnchor;
	@FXML
	ScrollPane ScrollPane;
	@FXML
	Label zoomLabel;
	@FXML
	Group group;
	@FXML
	Button pencilBtn;
	@FXML
	Button brushBtn;
	@FXML
	Button EraserBtn;
	@FXML
	Button bucketBtn;
	@FXML
	Button pickerBtn;
	@FXML
	Button rectBtn;
	@FXML
	Button roundRectBtn;
	@FXML
	Button ellipseBtn;
	@FXML
	ComboBox<String> sizeCombo;
	@FXML
	ColorPicker colorPicker1;
	@FXML
	ColorPicker colorPicker2;
	@FXML
	Button undoBtn;
	@FXML
	Button redoBtn;
	@FXML
	TextField heightTextField;
	@FXML
	TextField widthTextField;

	boolean zoomLocked = true;
	Button[] tools;
	Cursor cursor;
	String selectedTool = "";
	GraphicsContext gc;
	double prevX, prevY;
	Stack<CanvasHistory> undoStack;
	Stack<CanvasHistory> redoStack;
	int SizeVal = 1;
	Color color1, color2;
	File file;

	public void initialize() {
		gc = canvas.getGraphicsContext2D();
		Button[] tools = { pencilBtn, brushBtn, EraserBtn, bucketBtn, pickerBtn, rectBtn, roundRectBtn, ellipseBtn };
		this.tools = tools;
		bindSize();
		bindZoom();
		bindMouseXY();
		getCanvasHeighWidth();
		initializePinchZoom();
		initializeColors();
		initializeSizes();
		SetupDrawEvents();
		initializeHistory();
		disableEnableRedoUndo();
//		gc.setImageSmoothing(false);
	}

	private void initializeHistory() {
		undoStack = new Stack<>();
		redoStack = new Stack<>();
		if (undoStack.isEmpty())
			undoStack.add(new CanvasHistory(canvas.snapshot(null, null)));
	}

	private void SetupDrawEvents() {
		canvas.setOnMousePressed((e) -> {
			if (selectedTool.isEmpty())
				return;
			if (e.isPrimaryButtonDown()) {
				gc.setFill(color1);
				gc.setStroke(color1);
			}
			if (e.isSecondaryButtonDown()) {
				gc.setFill(color2);
				gc.setStroke(color2);
			}
			switch (selectedTool) {
			case "pencil":
				prevX = e.getX();
				prevY = e.getY();
				gc.strokeLine(prevX, prevY, prevX, prevY);
				break;
			case "brush":
				prevX = e.getX();
				prevY = e.getY();
				gc.fillArc(e.getX() - SizeVal, e.getY() - SizeVal, SizeVal, SizeVal, 0, 360, ArcType.ROUND);
				break;
			case "eraser":
				prevX = e.getX();
				prevY = e.getY();
				gc.setFill(Color.WHITE);
				gc.setStroke(Color.WHITE);
				gc.fillRect(e.getX(), e.getY(), SizeVal, SizeVal);
				break;
			case "bucket":
				if (e.isPrimaryButtonDown())
					bucketFill((int)e.getX(), (int)e.getY(), color1);
				if (e.isSecondaryButtonDown())
					bucketFill((int)e.getX(), (int)e.getY(), color2);
				break;
			case "picker":
				colorPicker1.setValue(
						canvas.snapshot(null, null).getPixelReader().getColor((int) e.getX(), (int) e.getY()));
				changeColor1();
				break;
			case "rect":
			case "roundrect":
			case "ellipse":
				prevX = e.getX();
				prevY = e.getY();
				break;
			}
		});

		canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, (e) -> {
			if (selectedTool.isEmpty())
				return;
			if (e.isPrimaryButtonDown()) {
				gc.setFill(color1);
				gc.setStroke(color1);
			}
			if (e.isSecondaryButtonDown()) {
				gc.setFill(color2);
				gc.setStroke(color2);
			}
			
			switch (selectedTool) {
			case "pencil":
				gc.strokeLine(prevX, prevY, e.getX(), e.getY());
				prevX = e.getX();
				prevY = e.getY();
				break;
			case "brush":
				if (Math.abs(e.getX() - prevX) > Math.max(SizeVal/2 ,1) || Math.abs(e.getY() - prevY) > Math.max(SizeVal/2 ,1)) {
					double myprevX = prevX;
					double myprevY = prevY;
					drawExtraPoints(myprevX, myprevY, e.getX(), e.getY());
				}
				gc.fillArc(e.getX() - SizeVal, e.getY() - SizeVal,  SizeVal,  SizeVal, 0, 360, ArcType.ROUND);
				prevX = e.getX();
				prevY = e.getY();
				break;
			case "eraser":
				gc.setFill(Color.WHITE);
				gc.setStroke(Color.WHITE);
				gc.fillRect(e.getX(), e.getY(), SizeVal, SizeVal);
				if (Math.abs(e.getX() - prevX) > Math.max(SizeVal/2 ,1)
						|| Math.abs(e.getY() - prevY) > Math.max(SizeVal/2 ,1)) {
					double myprevX = prevX;
					double myprevY = prevY;
					deleteExtraPoints(myprevX, myprevY, e.getX(), e.getY());
				}
				prevX = e.getX();
				prevY = e.getY();
				break;
			case "bucket":

				break;
			case "picker":

				break;
			case "rect":
				gc.drawImage(undoStack.peek().getImage(), 0, 0);
				gc.strokeRect(Math.min(prevX,e.getX()), Math.min(prevY,e.getY()),
						Math.abs(e.getX()-prevX), Math.abs(e.getY()-prevY));
				break;
			case "roundrect":
				gc.drawImage(undoStack.peek().getImage(), 0, 0);
				gc.strokeRoundRect(Math.min(prevX,e.getX()), Math.min(prevY,e.getY()),
						Math.abs(e.getX()-prevX), Math.abs(e.getY()-prevY),
						Math.min(Math.abs(e.getX()-prevX)/5, 50),Math.min(Math.abs(e.getX()-prevX)/5, 50));
				break;
			case "ellipse":
				gc.drawImage(undoStack.peek().getImage(), 0, 0);
				gc.strokeOval(Math.min(prevX,e.getX()), Math.min(prevY,e.getY()),
						Math.abs(e.getX()-prevX), Math.abs(e.getY()-prevY));
				break;
			default:

				break;
			}
		});
		canvas.setOnMouseReleased((e) -> {
			if (!compareImages(undoStack.peek().getImage(), (canvas.snapshot(null, null)))) {
				undoStack.add(new CanvasHistory(canvas.snapshot(null, null)));
				redoStack = new Stack();
				disableEnableRedoUndo();
			}
		});
	}

	private void bucketFill(int x, int y, Color fillColor) {
		WritableImage snapshot = canvas.snapshot(null, null);
		PixelReader pixelReader = snapshot.getPixelReader();
		LinkedList<PixelXY> queue = new LinkedList<>();
		queue.addLast(new PixelXY(x, y));
		while(!queue.isEmpty()) {
			PixelXY currentPixel = queue.pop();
			Color pixelColor = pixelReader.getColor(currentPixel.getX(), currentPixel.getY());
			if (!pixelColor.equals(fillColor)) {
				snapshot.getPixelWriter().setColor(currentPixel.getX(), currentPixel.getY(), fillColor);
				ArrayList<PixelXY> neighbors = getPixelNeighbors(currentPixel.getX(), currentPixel.getY(), snapshot);
				for (PixelXY neighbor : neighbors) {
					if (pixelReader.getColor(neighbor.getX(), neighbor.getY()).equals(pixelColor))
						queue.addLast(neighbor);
				}
			}
		}
		gc.drawImage(snapshot, 0, 0);
	}

	private ArrayList<PixelXY> getPixelNeighbors(int x, int y, WritableImage image) {
		ArrayList<PixelXY> neighbors = new ArrayList<>();
		if(x > 0)
			neighbors.add(new PixelXY(x - 1, y));
		if(x < image.getWidth() - 1)
			neighbors.add(new PixelXY(x + 1, y));
		if(y > 0)
			neighbors.add(new PixelXY(x, y - 1));
		if(y < image.getHeight() - 1)
			neighbors.add(new PixelXY(x, y + 1));

		return neighbors;
	}

	private void drawExtraPoints(double myprevX, double myprevY, double x, double y) {
		if (Math.abs(x - myprevX) >  Math.max(SizeVal/2 ,1) || Math.abs(y - myprevY) >  Math.max(SizeVal/2 ,1)) {

			double midx = (int) ((x + myprevX) / 2);
			double midy = (int) ((y + myprevY) / 2);

			gc.fillArc(midx - SizeVal, midy - SizeVal, SizeVal,  SizeVal, 0, 360, ArcType.ROUND);

			drawExtraPoints(myprevX, myprevY, midx, midy);
			drawExtraPoints(midx, midy, x, y);
		}
	}

	private void deleteExtraPoints(double myprevX, double myprevY, double x, double y) {
		if (Math.abs(x - myprevX) > Math.max(SizeVal/2 ,1) || Math.abs(y - myprevY) > Math.max(SizeVal/2 ,1)) {

			double midx = (int) ((x + myprevX) / 2);
			double midy = (int) ((y + myprevY) / 2);

			gc.setFill(Color.WHITE);
			gc.setStroke(Color.WHITE);
			gc.fillRect(midx, midy, SizeVal, SizeVal);

			deleteExtraPoints(myprevX, myprevY, midx, midy);
			deleteExtraPoints(midx, midy, x, y);
		}
	}

	private void initializeSizes() {
		ObservableList<String> options = FXCollections.observableArrayList("1 px", "3 px", "5 px", "8 px", "15 px");
		sizeCombo.setItems(options);
		sizeCombo.getSelectionModel().select(2);
		changeSize();

	}

	private void initializeColors() {
		colorPicker1.setValue(Color.BLACK);
		colorPicker2.setValue(Color.WHITE);
		color1 = colorPicker1.getValue();
		color2 = colorPicker2.getValue();
	}

	private void initializePinchZoom() {
		BigAnchor.setOnScroll((e) -> {
			if (e.isControlDown()) {
				if (e.getDeltaY() < 0)
					ScaleSlider.adjustValue(ScaleSlider.getValue() - 25);

				if (e.getDeltaY() > 0)
					ScaleSlider.adjustValue(ScaleSlider.getValue() + 25);
			}
		});
		CanvasAnchor.setOnScroll((e) -> {
			if (e.isControlDown()) {
				e.consume(); // prevent scrolling while zooming
				if (e.getDeltaY() < 0)
					ScaleSlider.adjustValue(ScaleSlider.getValue() - 25);
				if (e.getDeltaY() > 0)
					ScaleSlider.adjustValue(ScaleSlider.getValue() + 25);
			}
		});
	}

	private void getCanvasHeighWidth() {
		canvasWidthHeight.setText((int) canvas.getWidth() + " x " + (int) canvas.getHeight() + "px");
	}

	private void bindMouseXY() {
		canvas.setOnMouseMoved((e) -> {
			pixelXY.setText((int) e.getX() + ", " + (int) e.getY());
		});
		canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, (e) -> {
			pixelXY.setText((int) e.getX() + ", " + (int) e.getY());
		});
	}

	private void bindSize() {
		ScrollPane.maxHeightProperty().bind(BigAnchor.heightProperty());
		ScrollPane.maxWidthProperty().bind(BigAnchor.widthProperty());
		widthTextField.setText((int) canvas.getWidth() + "");
		heightTextField.setText((int) canvas.getHeight() + "");
	}

	private void bindZoom() {
		lockUnlock.setOnMouseClicked((e) -> {
			zoomLocked = !zoomLocked;
			if (zoomLocked) {
				lockUnlock.setImage(new Image(getClass().getResourceAsStream("/images/lock20.png")));
				ScaleSlider.setSnapToTicks(true);
			} else {
				lockUnlock.setImage(new Image(getClass().getResourceAsStream("/images/unlock20.png")));
				ScaleSlider.setSnapToTicks(false);
			}
		});
		ScaleSlider.valueProperty().addListener((e) -> {
			if (!zoomLocked || ScaleSlider.getValue() % 25 == 0) {
				zoomLabel.setText((int) (ScaleSlider.getValue()) + "%");
				double zoom = ScaleSlider.getValue() / 100.0;
				group.setScaleX(zoom);
				group.setScaleY(zoom);
			}

		});

		canvas.setOnMouseEntered((e) -> BigAnchor.setCursor(cursor));
		canvas.setOnMouseExited((e) -> BigAnchor.setCursor(Cursor.DEFAULT));
	}

	@FXML
	public void openFile() {
		ImageFxIO loader = new ImageFxIO((Stage) BigAnchor.getScene().getWindow());
		Object[] result = loader.oepnFromFile();
		if (result == null)
			return;
		Image image = (Image) result[0];
		File f = (File) result[1]; 
		int w = (int) image.getWidth();
		int h = (int) image.getHeight();
		canvas.setHeight(h);
		canvas.setWidth(w);
		CanvasAnchor.setPrefHeight(h);
		CanvasAnchor.setPrefWidth(w);
		getCanvasHeighWidth();

		undoStack = new Stack<>();
		redoStack = new Stack<>();
		disableEnableRedoUndo();
		initializeColors();
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		gc.drawImage(image, 0, 0);
		file = f;
		projectName.setText(file.getName());
	}

	@FXML
	public void saveFile() {	
		ImageFxIO saver = new ImageFxIO((Stage) BigAnchor.getScene().getWindow());
		Image image = canvas.snapshot(null, null);
		if (file == null) {
			File f = saver.saveToFile(image);
			if (f != null) {
				file = f;
				projectName.setText(file.getName());
			}
		} else {
			saver.saveToFile(image, file.getName().substring(file.getName().lastIndexOf(".")+1).toUpperCase(), file);
		}
	}

	@FXML
	public void saveAs() {
		ImageFxIO saver = new ImageFxIO((Stage) BigAnchor.getScene().getWindow());
		Image image = canvas.snapshot(null, null);
		File f = saver.saveToFile(image);
		if (f != null) {
			projectName.setText(f.getName());
			file = f;
		}

	}

	@FXML
	public void closeApp() {
		System.exit(0);
	}

	@FXML
	public void undo() {
		if (undoStack.size() > 1) {
			redoStack.add(undoStack.pop());
			if(undoStack.peek().DimensionsChanged()) {
				changeDimensions(undoStack.peek().getWidth(), undoStack.peek().getHeight());
			}
			gc.drawImage(undoStack.peek().getImage(), 0, 0);
		}
		disableEnableRedoUndo();
	}

	@FXML
	public void redo() {
		if (redoStack.size() > 0) {
			undoStack.add(redoStack.peek());
			CanvasHistory canvasHistory = redoStack.pop();
			if(canvasHistory.DimensionsChanged()) {
				changeDimensions(canvasHistory.getWidth(), canvasHistory.getHeight());
			}
			gc.drawImage(canvasHistory.getImage(), 0, 0);
		}
		disableEnableRedoUndo();
	}

	public void disableEnableRedoUndo() {
		redoBtn.setDisable(redoStack.isEmpty());
		undoBtn.setDisable(undoStack.size() < 2);
	}

	@FXML
	public void openAbout() {
		try {
			Desktop.getDesktop().browse(new URL("https://github.com/georgioyammine/PaintFX").toURI());
		}catch(Exception ignored){}
	}

	@FXML
	public void newFile() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		projectName.setText("Not Saved yet");
		file = null;
		initializeHistory();
		disableEnableRedoUndo();
	}

	@FXML
	public void selectTool(MouseEvent event) {
		Button b = (Button) event.getSource();
		b.getStyleClass().add("selected-tool");
		String id = b.getId();
		selectedTool = id;
		// if(selectedTool.equals("brush"))
		// gc.setGlobalAlpha(0.5);
		// else
		// gc.setGlobalAlpha(1);
		for (Button btn : tools) {
			if (btn != b) {
				btn.getStyleClass().remove("selected-tool");
			}
		}
		String imageName = "";
		cursor = new ImageCursor();
		switch (id) {
		case "pencil":
			imageName = "pencil";
			imageName += "32.png";
			cursor = new ImageCursor(new Image(getClass().getResourceAsStream("/images/" + imageName)),
					0, 32);
			break;
		case "brush":
			imageName = "paintbrush";
			imageName += "32.png";
			cursor = new ImageCursor(new Image(getClass().getResourceAsStream("/images/" + imageName)),
					6, 27);
			break;
		case "eraser":
			imageName = "eraser";
			imageName += "32.png";
			cursor = new ImageCursor(new Image(getClass().getResourceAsStream("/images/" + imageName)),
					10, 30);
			break;
		case "bucket":
			imageName = "fill";
			imageName += "32.png";
			cursor = new ImageCursor(new Image(getClass().getResourceAsStream("/images/" + imageName)),
					3, 28);
			break;
		case "picker":
			imageName = "pipette";
			imageName += "32.png";
			cursor = new ImageCursor(new Image(getClass().getResourceAsStream("/images/" + imageName)),
					4, 29);
			break;
		default:
			cursor = Cursor.CROSSHAIR;
			break;
		}

	}

	@FXML
	public void changeSize() {
		SizeVal = Integer
				.parseInt(sizeCombo.getValue().toString().substring(0, sizeCombo.getValue().toString().indexOf(" ")));
		gc.setLineWidth(SizeVal);
	}

	@FXML
	public void changeColor1() {
		color1 = colorPicker1.getValue();
	}

	@FXML
	public void changeColor2() {
		color2 = colorPicker2.getValue();
	}

	static public boolean compareImages(Image im1, Image im2) {
		for (int i = 0; i < im1.getWidth(); i++) {
			for (int j = 0; j < im1.getHeight(); j++) {
				if (!im1.getPixelReader().getColor(i, j).equals(im2.getPixelReader().getColor(i, j)))
					return false;
			}
		}
		return true;
	}

	@FXML
	public void discardDimensions() {
		widthTextField.setText((int) canvas.getWidth() + "");
		heightTextField.setText((int) canvas.getHeight() + "");
	}

	@FXML
	public void applyDimensions() {
		int w = Integer.parseInt(widthTextField.getText());
		int h = Integer.parseInt(heightTextField.getText());
		if(w < 1 || h < 1) {
			widthTextField.setText((int) canvas.getWidth() + "");
			heightTextField.setText((int) canvas.getHeight() + "");
			return;
		}

		undoStack.add(new CanvasHistory((int)canvas.getWidth(), (int)canvas.getHeight(),
				undoStack.pop().getImage()));

		changeDimensions(w, h);

		undoStack.add(new CanvasHistory((int)canvas.getWidth(), (int)canvas.getHeight(),
				canvas.snapshot(null, null)));
		disableEnableRedoUndo();
	}

	private void changeDimensions(int w, int h) {
		try {
			canvas.setHeight(h);
			canvas.setWidth(w);
			CanvasAnchor.setPrefHeight(h);
			CanvasAnchor.setPrefWidth(w);
			getCanvasHeighWidth();
			double zoom = ScaleSlider.getValue() / 100.0;
			group.setScaleX(zoom+0.01);
			group.setScaleY(zoom+0.01);
			Thread.sleep(1);
			group.setScaleX(zoom);
			group.setScaleY(zoom);
		} catch (InterruptedException e) {
			widthTextField.setText((int) canvas.getWidth() + "");
			heightTextField.setText((int) canvas.getHeight() + "");
		}
	}

	@FXML
	public void exitedCanvasTab() {
		discardDimensions();
	}

	@FXML public void checkEscape(KeyEvent e) {
		if(e.getCode()== KeyCode.ESCAPE) {
			gc.drawImage(undoStack.peek().getImage(), 0, 0);
		}
	}

}
