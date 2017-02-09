package view;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import controller.EqualsController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import model.EqualsModel;
import resources.I18n;

public class ViewLoader {
	
	public static Parent create(URL fxmlUrl, EqualsModel model, EqualsController controller) throws MalformedURLException {
		FXMLLoader loader;
		loader = new FXMLLoader(fxmlUrl, I18n.getResourceBundle());
		Parent parent = null;
		try {
			parent = (Parent)loader.load();
		} catch (IOException e) {
			System.out.println("Could not load: " + e.getMessage());
		}
		loader.<EqualsView>getController().init(model, controller);
		return parent;
	}
	
}
