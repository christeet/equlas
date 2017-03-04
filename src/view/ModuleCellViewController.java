package view;

import data.Course;
import data.Module;
import data.UserRole;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

public class ModuleCellViewController extends EqualsView {

	@FXML private ImageView userRoleIcon;
	@FXML private Label moduleTitleLabel;
	@FXML private Label semesterLabel;
	@FXML private ListView<Course> coursesList;

	private static double maxWidth = 0;
	private Module module;
	
	@FXML
	protected void initialize() {
		showCoursesList(false);
	}
	
	@Override
	public void init() {
	}
	
	public void setModule(Module module) {
		this.module = module;
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
		maxWidth = 0;
		coursesList.setCellFactory(new Callback<ListView<Course>, ListCell<Course>>() {
            @Override
            public ListCell<Course> call(ListView<Course> listView) {
            	ListCell<Course> lc = new ListCell<Course>() {

                    @Override
                    protected void updateItem(Course cellCourse, boolean bln) {
                		Platform.runLater(() -> {
	                        super.updateItem(cellCourse, bln);
	                        if (cellCourse != null) {

                        		CourseCellViewController cellView = (CourseCellViewController) ViewLoader.create(
                        						getClass().getResource("CourseCellView.fxml"),
                        						model, 
                        						controller);
                        		cellView.setModuleAndCourse(module, cellCourse);
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
		
        coursesList.setItems(model.getCoursesListProperty()
        		.filtered(predicate -> predicate.getModuleId() == this.module.getId()));
        
        if(!coursesList.getItems().isEmpty()) {
        	// TODO: remove magic number:
        	coursesList.setPrefHeight(coursesList.getItems().size() * 60 + 2);
        	showCoursesList(true);
        }
	}
	
	private void showCoursesList(boolean show) {
		coursesList.setVisible(show);
		coursesList.setDisable(!show);
		coursesList.setMaxHeight((show) ? (-1) : (0) );
	}
	
	@Override
	public void dispose() {
		
	}

}
