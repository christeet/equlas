package view;

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
import javafx.util.Callback;

public class SelectViewController extends EqualsView {

	private static double maxWidth = 0;
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
	private void initEntityListBindings() {
		entityList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE); 
		entityList.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Module>() {
		    @Override
		    public void onChanged(Change<? extends Module> change) {
		    	onSelectedModuleChanged(entityList.getSelectionModel().getSelectedItem());
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
            	ListCell<Module> lc = new ListCell<Module>() {

                    @Override
                    protected void updateItem(Module cellModule, boolean bln) {
                		Platform.runLater(() -> {
	                        super.updateItem(cellModule, bln);
	                        if (cellModule != null) {

                        		ModuleCellViewController cellView = (ModuleCellViewController) ViewLoader.create(
                        						getClass().getResource("ModuleCellView.fxml"),
                        						model, 
                        						controller);
                        		cellView.setModule(cellModule);
                        		setGraphic(cellView.getRootNode());
	                        }
	                        this.widthProperty().addListener((obs,old,value) -> {
	                            //System.out.format("width of %s: %f\r\n", obs.toString(), value);
	                            maxWidth = Math.max(maxWidth, (double)value);
	                            listView.setPrefWidth(maxWidth);
	                        });
                		});
                    }
                };
                return lc;
            }

        });

	}
	
	private void onSelectedModuleChanged(Module selectedModule) {
		controller.selectedModuleChanged(selectedModule);
    	System.out.format("selected Module %s\r\n",entityList.getSelectionModel().getSelectedItem());
    	setContent(entityList.getSelectionModel().getSelectedItem());
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
		EqualsView newView = ViewLoader.create(getClass().getResource(filename), model, controller);
		container.getChildren().clear();
		container.getChildren().add(newView.getRootNode());
		currentView = newView;
		Parent rootNode = newView.getRootNode();
		AnchorPane.setTopAnchor(rootNode, 0.0);
		AnchorPane.setBottomAnchor(rootNode, 0.0);
		AnchorPane.setRightAnchor(rootNode, 0.0);
		AnchorPane.setLeftAnchor(rootNode, 0.0);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
}
