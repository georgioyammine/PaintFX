package launcher;

import java.util.Arrays;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.JMetroStyleClass;
import jfxtras.styles.jmetro.Style;

public class Main extends Application {
	static String launchName = "paintFX.fxml";

	@Override
	public void start(Stage stage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/views/" + launchName));
		Scene scene = new Scene(root, 1100, 550);
		new JMetro(scene, Style.LIGHT);
		scene.getRoot().getStyleClass().add(JMetroStyleClass.BACKGROUND);
		stage.setScene(scene);
		stage.setTitle("paintFX");
		stage.getIcons().add(new Image(getClass().getResource("/images/logo2.png").openStream()));
		stage.show();
		stage.setMinWidth(1011);
		stage.setMinHeight(400);
	}

	public static void main(String[] args) {
		if (args.length == 0)
			launch(args);
		else { // this is the method to allow this app to be used as the default app to open an image
			if (args[0].isEmpty())
				launch(args);
			else {
				try {
					System.out.println(Arrays.toString(args));
					StringBuilder sb = new StringBuilder();
					for (String str : args)
						sb.append(str + " ");
					sb.delete(sb.length() - 1, sb.length());
					handleOpenImage(sb.toString());
				} catch (Exception e) {
					e.printStackTrace();
					launch(args);
				}
			}
		}
	}

	static void handleOpenImage(String str) {
		launch(new String[0]);
	}
}
