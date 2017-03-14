package view;

import java.io.IOException;
import java.net.URL;

import controller.EqualsController;
import equals.EqualsModel;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import resources.I18n;

public class ViewLoader {
	
	public static EqualsView create(
			URL fxmlUrl, 
			EqualsModel model, 
			EqualsController controller,
			HostServices hostServices) {
		FXMLLoader loader;
		Parent rootNode = null;
		EqualsView view = null;
		try {
			loader = new FXMLLoader(fxmlUrl, I18n.getResourceBundle());
			rootNode = (Parent)loader.load();
			view = loader.<EqualsView>getController();
			view.init(rootNode, model, controller, hostServices);
		} catch (IOException e) {
			System.out.println("Could not load: " + e.getMessage());
			e.printStackTrace();
		}
		return view;
	}
	
}
