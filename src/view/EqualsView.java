package view;

import controller.EqualsController;
import equals.EqualsModel;
import javafx.scene.Parent;

/**
 * For the {@link ViewLoader} to easily create a View, the View's fx:controller must
 * inherit from the abstract class {@link EqualsView}.
 * @author sbol
 *
 */
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

	/**
	 * Returns the hierarchically uppermost node of the scene-graph of this view.
	 * @return the root-Node
	 */
	public Parent getRootNode() { return rootNode; }
}
