package view;

import controller.EqualsController;
import javafx.scene.Parent;
import model.EqualsModel;

public abstract class EqualsView {
	protected Parent parentNode;
	protected EqualsModel model;
	protected EqualsController controller;
	
	/**
	 * Implement this method to initialize bindings to the model.
	 */
	protected abstract void init();
	
	/**
	 * Implement this method to clean up bindings to the model, before this view gets destroyed.
	 */
	public abstract void dispose();
	
	/**
	 * This method gets called automatically by ViewLoader.create()
	 * @param parentNode The hierarchically uppermost node of the scene-graph of the freshly created view.
	 * @param model the EqualsModel to which we may create bindings.
	 * @param controller the EqualsController, to whom we may delegate commands.
	 */
	public void init(Parent parentNode, EqualsModel model, EqualsController controller) {
		this.parentNode = parentNode;
		this.model = model;
		this.controller = controller;
		init();
	}

	public Parent getParentNode() { return parentNode; }
}
