package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.UserLogin;
import resources.I18n;

public class UserLoginController {
	
	@FXML
	private TextField usernameField;
	
	@FXML
	private PasswordField passwordField;
	
	@FXML
	private Button checkCredentials;
	
	@FXML
	private Label statusLabel;

	@FXML
	protected void checkUserCredentials() {
		if(this.login.checkPassword(passwordField.getText(), usernameField.getText())) {
			this.statusLabel.setStyle("-fx-text-fill: green;");
			this.statusLabel.setText(I18n.getString("login.message.success"));
		} else {
			this.statusLabel.setStyle("-fx-font-fill: red;");
			this.statusLabel.setText(I18n.getString("login.message.failed"));
		}
	}
	
	private UserLogin login;
	
	public void init(UserLogin login) {
		this.login = login;
	}

}
