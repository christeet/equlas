package login;

import javafx.application.Application;
import javafx.stage.Stage;
import model.UserLogin;

public class UserLoginMain extends Application {
	
	private UserLogin login;
	
	public UserLoginMain() {
		this.login = new UserLogin();
	}
	
	/**
	 * The entry-point of this program.
	 * @param args commandline arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		login.show();
	}
	
}