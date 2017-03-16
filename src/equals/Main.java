package equals;

import java.util.Locale;

import controller.EqualsController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import resources.I18n;
import util.Prefs;
import view.EqualsView;
import view.ViewLoader;

public class Main extends Application {
	
	@Override
	public void start(Stage stage) {
		
		// get locale as set in properties file (or system default)
		I18n.setLocale(Locale.forLanguageTag(Prefs.get().getLocale()));
		
		EqualsModel model = new EqualsModel();
		EqualsController controller = new EqualsController(model);

		EqualsView mainWindow = ViewLoader.create(
				"MainContainerView.fxml"
				, model, controller);
		
		Scene scene = new Scene(mainWindow.getRootNode(), 
							Prefs.get().getWindowWidth(), 
							Prefs.get().getWindowHeight());
		
		scene.widthProperty().addListener((obs, old, newSceneWidth) -> {
			if(!stage.isMaximized()) {
				Prefs.get().setWindowWidth((double)newSceneWidth);
			}
		});
		
		scene.heightProperty().addListener((obs, old, newSceneHeight) -> {
			if(!stage.isMaximized()) {
				Prefs.get().setWindowHeight((double)newSceneHeight);
			}
		});
		
		Platform.setImplicitExit(false);
		stage.setOnCloseRequest((event) -> {
			Prefs.save();
            System.out.println("Saved preferences. Goodbye!");
            Platform.exit();
        });

		stage.setMaximized(Prefs.get().getMaximized());
		stage.maximizedProperty().addListener((obs, old, maximized) -> {
			Prefs.get().setMaximized(maximized);
		});
		stage.setTitle(I18n.getString("login.title"));
		stage.setScene(scene);
		stage.show();
	}
	
	/**
	 * The entry-point of this program.
	 * @param args commandline arguments
	 */
	public static void main(String[] args) {
		launch(args);
		//consoleDebug();
	}
}
