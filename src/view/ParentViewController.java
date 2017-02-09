package view;

import controller.EqualsController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import model.EqualsModel;
import model.UserLogin;
import util.IObservable;
import util.IObserver;

public class ParentViewController implements EqualsView, IObserver<UserLogin> {
	
	
	@FXML private BorderPane container;
	private EqualsModel model;
	private EqualsController controller;
	
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
	public void update(UserLogin userLogin, Object arg) {
		
	}
}
