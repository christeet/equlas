package view;

import java.net.MalformedURLException;

import data.Module;
import data.Person;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class SelectViewController extends EqualsView {

	@FXML private Label userName;
	@FXML private AnchorPane container;
	@FXML private ListView<Module> entityList;

	private EqualsView currentView;
	
	@Override
	protected void init() {
		Platform.runLater(() -> {
			Person p = model.getUserLogin().getUser();
			userName.setText(p.getName());
		});
		initEntityListBindings();
	}
	
	/**
	 * Binds the entityList ListView to the module-list property of the EqualsModel,
	 * registers to the change in selection of the items
	 * and automatically selects the first item in the list.
	 */
	static double maxWidth = 0;
	private void initEntityListBindings() {
		entityList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE); 
		entityList.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Module>() {
		    @Override
		    public void onChanged(Change<? extends Module> change) {
		    	System.out.format("selected %s\r\n",entityList.getSelectionModel().getSelectedItem());
		    	setContent(entityList.getSelectionModel().getSelectedItem());
		    }
		});
		entityList.setItems(model.getModuleListProperty());
		Platform.runLater(() -> {
			entityList.getSelectionModel().select(0);
		});
		maxWidth=0;
		entityList.setCellFactory(new Callback<ListView<Module>, ListCell<Module>>() {
            @Override
            public ListCell<Module> call(ListView<Module> listView) {
                return new ListCell<Module>() {

                    @Override
                    protected void updateItem(Module item, boolean bln) {
                		Platform.runLater(() -> {
	                        super.updateItem(item, bln);
	                        if (item != null) {
	                            VBox vBox = new VBox(new Text(item.getShortName()), 
	                            		new Text(item.getName()));
	                            HBox hBox = new HBox(new Label(item.getUserRole().name()), vBox);
	                            hBox.setSpacing(10);
	                            setGraphic(hBox);
		                        maxWidth = Math.max(maxWidth, this.getWidth());
		                        System.out.format("width: %f\r\n", maxWidth);
		                        listView.setMinWidth(maxWidth);
	                        }
                		});
                    }

                };
            }

        });

	}
	
	private void setContent(Module selectedModule) {
		switch(selectedModule.getUserRole()) {
		case ASSISTANT:
			setContentView("CasAssistantView.fxml");
			break;
		case HEAD:
			setContentView("CasResponsibleView.fxml");
			break;
		case STUDENT:
			setContentView("StudentView.fxml");
			break;
		case TEACHER:
			setContentView("TeacherView.fxml");
			break;
		default:
			break;
		}
	}
	
	private void setContentView(String filename) {
		if(currentView != null)  {
			currentView.dispose();
		}
		try {
			EqualsView newView = ViewLoader.create(getClass().getResource(filename), model, controller);
			container.getChildren().clear();
			container.getChildren().add(newView.getRootNode());
			currentView = newView;
			Parent rootNode = newView.getRootNode();
			AnchorPane.setTopAnchor(rootNode, 0.0);
			AnchorPane.setBottomAnchor(rootNode, 0.0);
			AnchorPane.setRightAnchor(rootNode, 0.0);
			AnchorPane.setLeftAnchor(rootNode, 0.0);
			/*AnchorPane n = (AnchorPane)newView.getRootNode();
			if(n != null){
			}*/
			//n.prefWidthProperty().bind(container.prefWidthProperty());
			//newView.getRootNode().layoutYProperty().bind(container.layoutYProperty());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
}
