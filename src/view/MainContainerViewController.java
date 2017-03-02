package view;

import java.util.Locale;

import javafx.beans.property.Property;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import model.UserLogin;
import resources.I18n;
import util.IObserver;
import util.Prefs;

public class MainContainerViewController extends EqualsView implements IObserver<UserLogin> {

	private EqualsView currentView;
	
	@FXML private BorderPane container;
	@FXML private ToggleButton toggleDe;
	@FXML private ToggleButton toggleEn;
	@FXML private ToggleGroup toggleGroupLanguage;
	

	@FXML protected void initialize() {
		toggleDe.setUserData("de-DE");
		toggleEn.setUserData("en-US");
		toggleGroupLanguage.getToggles().filtered(t -> t.getUserData().equals(Prefs.get().getLocale()))
										.forEach(toggle -> toggle.setSelected(true));

		toggleGroupLanguage.selectedToggleProperty().addListener((obs, old, toggleBtn) -> {
			if(toggleBtn != null) {
				String newLocaleTag = (String)toggleBtn.getUserData();
				System.out.format("selected %s\r\n", newLocaleTag);
				Prefs.get().setLocale(newLocaleTag);
				Prefs.save();
				I18n.setLocale(Locale.forLanguageTag(newLocaleTag));
			}
			else {
				old.setSelected(true);
			}
		});
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
