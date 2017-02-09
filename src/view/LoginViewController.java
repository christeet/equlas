package view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.UserLogin;
import resources.I18n;
import util.IObserver;

public class LoginViewController extends EqualsView implements IObserver<UserLogin> {
	
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
		this.controller.loginDataEntered(passwordField.getText(), usernameField.getText());
	}

	@Override
	protected void init() {
		model.getUserLogin().addObserver(this);
	}


	@Override
	public void dispose() {
		model.getUserLogin().deleteObserver(this);
	}


	@Override
	public void update(UserLogin o) {
		switch(o.getLoginState()) {
		case LOGGED_IN:
			this.statusLabel.setStyle("-fx-text-fill: green;");
			this.statusLabel.setText(I18n.getString("login.message.success"));
			break;
		case LOGGED_OUT:
			this.statusLabel.setText("");
			break;
		case LOGIN_FAILED:
			this.statusLabel.setStyle("-fx-font-fill: red;");
			this.statusLabel.setText(I18n.getString("login.message.failed"));
			break;
		default:
			break;
		}
	}

}
