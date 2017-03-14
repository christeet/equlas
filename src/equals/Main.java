package equals;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import controller.EqualsController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import resources.I18n;
import util.Prefs;
import view.EqualsView;
import view.ViewLoader;

public class Main extends Application {

	private ScheduledExecutorService preferencesSaveExecutor = Executors.newScheduledThreadPool(1);
	private ScheduledFuture<?> preferencesSaveTask = null;
	
	@Override
	public void start(Stage stage) {
		
		// get locale as set in properties file (or system default)
		I18n.setLocale(Locale.forLanguageTag(Prefs.get().getLocale()));
		
		EqualsModel model = new EqualsModel();
		EqualsController controller = new EqualsController(model);

		EqualsView mainWindow = ViewLoader.create(getClass().getResource(
				"../view/MainContainerView.fxml")
				, model, controller, this.getHostServices());
		
		Scene scene = new Scene(mainWindow.getRootNode(), Prefs.get().getWindowWidth(), Prefs.get().getWindowHeight());
		
		scene.widthProperty().addListener((obs, old, newSceneWidth) -> {
			if(!stage.isMaximized()) {
				Prefs.get().setWindowWidth((double)newSceneWidth);
				savePreferencesDelayed();
			}
		});
		
		scene.heightProperty().addListener((obs, old, newSceneHeight) -> {
			if(!stage.isMaximized()) {
				Prefs.get().setWindowHeight((double)newSceneHeight);
				savePreferencesDelayed();
			}
		});

		stage.setMaximized(Prefs.get().getMaximized());
		stage.maximizedProperty().addListener((obs, old, maximized) -> {
			Prefs.get().setMaximized(maximized);
			Prefs.save();
		});
		stage.setTitle(I18n.getString("login.title"));
		stage.setScene(scene);
		stage.show();
		
		scene.focusOwnerProperty().addListener((obs, old, focusOwner) -> {
			System.out.format("Focus is on %s\r\n", focusOwner);
		});
	}
	/**
	 * The entry-point of this program.
	 * @param args commandline arguments
	 */
	public static void main(String[] args) {
		launch(args);
		//consoleDebug();
	}

	private void savePreferencesDelayed() {
		if(null != preferencesSaveTask) preferencesSaveTask.cancel(false);
		preferencesSaveTask = preferencesSaveExecutor.schedule(() -> {
			Prefs.save();
		}, 1, TimeUnit.SECONDS);
	}
}
