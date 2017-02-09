package model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class EqualsModel {

	private BooleanProperty loggedIn = new SimpleBooleanProperty();
	private UserLogin userLogin;
	
	public EqualsModel() {
		userLogin = new UserLogin();
	}
	
	public BooleanProperty LoggedInProperty() {
		return loggedIn;
	}
	
	public UserLogin getUserLogin() { return userLogin; }
	
}
