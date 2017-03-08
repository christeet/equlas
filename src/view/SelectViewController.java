package view;

import java.util.ArrayList;

import data.Course;
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

	@FXML private Label userName;
	@FXML private AnchorPane container;
	@FXML private ListView<Module> entityList;

	private ArrayList<ModuleCellViewController> moduleCellViews = new ArrayList<>();
	private EqualsView currentView;
	
	@Override
	protected void init() {
		Platform.runLater(() -> {
			Person p = model.getUserLogin().getUser();
			userName.setText(p.getName());
			entityList.setPrefWidth(450);
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
			// select first module by default:
			entityList.requestFocus();
			entityList.getSelectionModel().select(0);
		});
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
                        		cellView.setModule(this, cellModule, selectedCourse -> {
                        			onSelectedCourseChanged(cellModule, selectedCourse);
                        		});
                        		setGraphic(cellView.getRootNode());
                        		moduleCellViews.add(cellView);
	                        }
                		});
                    }
                };
                return lc;
            }
        });
		
		entityList.focusedProperty().addListener((obs, focusedBefore, focusedNow) -> {
			if(!focusedBefore && focusedNow) {
				int index = entityList.getSelectionModel().getSelectedIndex();
				entityList.getSelectionModel().clearSelection();
				entityList.getSelectionModel().select(index);
			}
		});
	}
	
	private void onSelectedModuleChanged(Module selectedModule) {
		if(selectedModule == null) return;
		controller.selectedModuleChanged(selectedModule);
		if(entityList.isFocused()) {
	    	System.out.format("***** selected Module: %s\r\n",
	    			entityList.getSelectionModel().getSelectedItem().getShortName());
			moduleCellViews.stream().filter(m->m.getModule() == selectedModule).forEach(m -> m.deselectCourses());
	    	setModuleRelatedContent(selectedModule);
		} else {
	    	System.out.format("**** selected a Course of Module: %s\r\n",
	    			entityList.getSelectionModel().getSelectedItem().getShortName());
		}
	}
	
	private void onSelectedCourseChanged(Module parentModule, Course selectedCourse) {
		if(parentModule == null || selectedCourse == null) return;
		moduleCellViews.stream().filter(m->m.getModule() != parentModule).forEach(m -> m.deselectCourses());
		entityList.getSelectionModel().select(parentModule);
		controller.selectedCourseChanged(selectedCourse);
		setCourseRelatedContent(parentModule, selectedCourse);
	}
	
	private void setModuleRelatedContent(Module selectedModule) {
		System.out.format("UserRole for Module = %s\r\n", selectedModule.getUserRole().name());
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
			setContentView("CasResponsibleView.fxml");
			break;
		default:
			break;
		}
	}
	
	private void setCourseRelatedContent(Module parentModule, Course selectedCourse) {
		System.out.format("UserRole for Module containing Course = %s\r\n", parentModule.getUserRole().name());
		switch(parentModule.getUserRole()) {
		case ASSISTANT:
			setContentView("CasAssistantView.fxml");
			break;
		case HEAD:
		case TEACHER:
			setContentView("TeacherView.fxml");
			TeacherViewController view = (TeacherViewController)currentView;
			if(view != null) {
				view.setCourse(selectedCourse);
			}
			break;
		default:
			System.err.println("wrong state!");
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
