package view;

import controller.EqualsController;
import javafx.scene.Parent;
import model.EqualsModel;

public abstract class EqualsView {
	protected Parent rootNode;
	protected EqualsModel model;
	protected EqualsController controller;
	
	/**
	 * Implement this method to initialize bindings to the model.
	 * Caution: this method may not necessarily be called from the JavaFX GUI-Thread!
	 */
	protected abstract void init();
	
	/**
	 * Implement this method to clean up bindings to the model, before this view gets destroyed.
	 * Caution: this method may not necessarily be called from the JavaFX GUI-Thread!
	 */
	public abstract void dispose();
	
	/**
	 * This method gets called automatically by ViewLoader.create()
	 * @param rootNode The hierarchically uppermost node of the scene-graph of the freshly created view.
	 * @param model the EqualsModel to which we may create bindings.
	 * @param controller the EqualsController, to whom we may delegate commands.
	 */
	public void init(Parent rootNode, EqualsModel model, EqualsController controller) {
		this.rootNode = rootNode;
		this.model = model;
		this.controller = controller;
		init();
	}

	public Parent getRootNode() { return rootNode; }
}
