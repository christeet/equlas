package view;

import java.io.IOException;

import controller.EqualsController;
import equals.EqualsModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import resources.I18n;

public class ViewLoader {
	
	public static EqualsView create(
			String filename, 
			EqualsModel model, 
			EqualsController controller) {
		FXMLLoader loader;
		Parent rootNode = null;
		EqualsView view = null;
		try {
			loader = new FXMLLoader(ViewLoader.class.getResource(filename), I18n.getResourceBundle());
			rootNode = (Parent)loader.load();
			view = loader.<EqualsView>getController();
			view.init(rootNode, model, controller);
		} catch (IOException e) {
			System.out.println("Could not load: " + e.getMessage());
			e.printStackTrace();
		}
		return view;
	}
	
}
