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
	
	public static EqualsView create(
			URL fxmlUrl, 
			EqualsModel model, 
			EqualsController controller) throws MalformedURLException {
		FXMLLoader loader;
		loader = new FXMLLoader(fxmlUrl, I18n.getResourceBundle());
		Parent rootNode = null;
		try {
			rootNode = (Parent)loader.load();
		} catch (IOException e) {
			System.out.println("Could not load: " + e.getMessage());
		}
		EqualsView view = loader.<EqualsView>getController();
		view.init(rootNode, model, controller);
		return view;
	}
	
}
