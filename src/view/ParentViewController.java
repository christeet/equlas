package view;

import java.net.MalformedURLException;

import controller.EqualsController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import model.EqualsModel;
import model.UserLogin;
import util.IObserver;

public class ParentViewController implements EqualsView, IObserver<UserLogin> {
	
	
	@FXML private BorderPane container;
	private EqualsModel model;
	private EqualsController controller;
	private Parent currentView;
	
	public void setContentView(Parent content) {
		container.setCenter(content);
		//setTitle(I18n.getString("login.title"));
	}
	
	public void init(EqualsModel model, EqualsController controller) {
		this.model = model;
		this.controller = controller;
		
		this.model.getUserLogin().addObserver(this);
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
		try {
			currentView = ViewLoader.create(getClass().getResource("LoginView.fxml"), model, controller);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setContentView(currentView);
	}
	
	private void displaySelectView() {
		try {
			currentView = ViewLoader.create(getClass().getResource("SelectView.fxml"), model, controller);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setContentView(currentView);
	}
	
}
