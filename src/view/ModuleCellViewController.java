package view;

import data.Course;
import data.Module;
import data.UserRole;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

public class ModuleCellViewController extends EqualsView {

	@FXML private ImageView userRoleIcon;
	@FXML private Label moduleTitleLabel;
	@FXML private Label semesterLabel;
	@FXML private ListView<Course> coursesList;

	private ListCell<Module> containingCell;
	private Module module;
	private CourseSelector courseSelector;
	private ObservableList<CourseCellViewController> courseCellViews = FXCollections.observableArrayList();
	
	@FXML
	protected void initialize() {
		showCoursesList(false);
	}
	
	@Override
	public void init() {
	}
	
	public void setModule(ListCell<Module> cell, Module module, CourseSelector courseSelector) {
		this.containingCell = cell;
		this.module = module;
		this.courseSelector = courseSelector;
		this.moduleTitleLabel.setText(module.getName());
		this.semesterLabel.setText(module.getShortName());
		setImageByUserRole(module.getUserRole());
		
		
		switch(this.module.getUserRole()) {
		case ASSISTANT:
			break;
		case HEAD:
			setCoursesList();
			break;
		case STUDENT:
			break;
		case TEACHER:
			setCoursesList();
			break;
		default:
			break;
		
		}
	}
	
	public Module getModule() {
		return module;
	}
	
	public void deselectCourses() {
		coursesList.getSelectionModel().clearSelection();
	}
	
	public ObservableList<CourseCellViewController> getCourseCellViews() {
		return courseCellViews;
	}
	
	private void setImageByUserRole(UserRole userRole) {
		String imageFilename = null;
		switch(userRole) {
		case ASSISTANT:
			imageFilename = "assistant.png";
			break;
		case HEAD:
			imageFilename = "head.png";
			break;
		case STUDENT:
			imageFilename = "student.png";
			break;
		case TEACHER:
			imageFilename = "teacher.png";
			break;
		default:
			imageFilename = null;
			break;
		}
		if(imageFilename != null) {
			userRoleIcon.setImage(new Image("file:src/resources/" + imageFilename));
		}
	}
	
	private void setCoursesList() {
		coursesList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE); 
		coursesList.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Course>() {
		    @Override
		    public void onChanged(Change<? extends Course> change) {
		    	onSelectedCourseChanged(coursesList.getSelectionModel().getSelectedItem());
		    }
		});
		
		coursesList.setCellFactory(new Callback<ListView<Course>, ListCell<Course>>() {
            @Override
            public ListCell<Course> call(ListView<Course> listView) {
            	ListCell<Course> lc = new ListCell<Course>() {

                    @Override
                    protected void updateItem(Course cellCourse, boolean empty) {
                		Platform.runLater(() -> {
	                        super.updateItem(cellCourse, empty);
	                        if (cellCourse != null && !empty) {

                        		CourseCellViewController cellView = (CourseCellViewController) ViewLoader.create(
                        				"CourseCellView.fxml",
                        						model, 
                        						controller);
                        		cellView.setModuleAndCourse(this, module, cellCourse);
                        		Node rootNode = cellView.getRootNode();
                        		setGraphic(rootNode);
                        		registerCellView(cellView);
	                        }
                		});
                    }
                };
                return lc;
            }

        });
		
		if(module.getUserRole() == UserRole.TEACHER) {
			System.out.format("I'm a TEACHER for Module %s\r\n", module.getShortName());
			final int currentTeacher = model.getUserLogin().getUser().getId(); 
	        coursesList.setItems(model.getCoursesListProperty()
	        		.filtered(course -> course.getModuleId() == this.module.getId()
	        						&& course.getTeacherId() == currentTeacher));
		} else {
			System.out.format("I'm a NOT a teacher for Module %s\r\n", module.getShortName());
	        coursesList.setItems(model.getCoursesListProperty()
	        		.filtered(course -> course.getModuleId() == this.module.getId()));
		}
        
        if(!coursesList.getItems().isEmpty()) {
        	// TODO: remove magic number:
        	coursesList.setPrefHeight(coursesList.getItems().size() * 58 + 2);
        	showCoursesList(true);
        }

		containingCell.selectedProperty().addListener((obs, old, selected) -> updateSelection());
		courseCellViews.addListener(
				(ListChangeListener.Change<? extends CourseCellViewController> change)  -> setInitialSelection());
	}
	
	/* selects Course from coursesList by default when clicking on a ModuleCell, IF it is the only course. */
	private void updateSelection() {
		if(containingCell.isSelected() 
		&& containingCell.getListView().isFocused()
		&& courseCellViews.size() == 1) {
				courseCellViews.get(0).selectCell();
		}
	}
	
	/* as courseCellViews get added to the list, check if the the currently selected ModuleCell has exactly 1 course
	 * and select it by default, if so.*/
	private void setInitialSelection() {
		if(containingCell.isSelected()) {
			if(courseCellViews.size() == 1) {
				courseCellViews.get(0).selectCell();
			} else {
				containingCell.getListView().requestFocus();
			}
		}
	}
	
	private void registerCellView(CourseCellViewController cellView) {
		/* remove "old" CourseCellViewControllers associated with the same course */
		courseCellViews.removeIf(cv -> cv.getCourse().equals(cellView.getCourse()));
		courseCellViews.add(cellView);
	}
	
	private void onSelectedCourseChanged(Course selectedCourse) {
		if(selectedCourse == null) return;
		coursesList.requestFocus();
		courseSelector.courseSelected(selectedCourse);
    	System.out.format("selected Course: %s\r\n",coursesList.getSelectionModel().getSelectedItem().getShortName());
    	//setContent(coursesList.getSelectionModel().getSelectedItem());
	}
	
	private void showCoursesList(boolean show) {
		coursesList.setVisible(show);
		coursesList.setDisable(!show);
		coursesList.setMaxHeight((show) ? (-1) : (0));
	}
	
	@Override
	public void dispose() {
		
	}

}
