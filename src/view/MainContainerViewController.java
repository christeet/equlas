package view;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import model.UserLogin;
import util.IObserver;

public class MainContainerViewController extends EqualsView implements IObserver<UserLogin> {

	private EqualsView currentView;
	
	@FXML private BorderPane container;

	@Override
	protected void init() {
		model.getUserLogin().addObserver(this);
	}

	@Override
	public void dispose() {
		model.getUserLogin().deleteObserver(this);
	}
	
	@Override
	public void update(UserLogin userLogin) {
		switch(userLogin.getLoginState()) {
		case LOGGED_IN:
			displaySelectView();
			break;
		case LOGGED_OUT:
			displayLoginView();
			break;
		case LOGIN_FAILED:
			break;
		default:
			break;
		}
	}

	private void displayLoginView() {
		setContentView("LoginView.fxml");
	}
	
	private void displaySelectView() {
		setContentView("SelectView.fxml");
	}
	
	private void setContentView(String filename) {
		if(currentView != null) {
			currentView.dispose();
		}
		EqualsView newView = ViewLoader.create(getClass().getResource(filename), model, controller);
		container.setCenter(newView.getRootNode());
		currentView = newView;
	}
	
}
