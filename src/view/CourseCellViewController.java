package view;

import data.Course;
import data.Module;
import data.UserRole;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class CourseCellViewController extends EqualsView {

	@FXML private ImageView userRoleIcon;
	@FXML private Label courseTitleLabel;
	@FXML private Label semesterLabel;

	private Module module;
	private Course course;
	
	@FXML
	protected void initialize() {
	}
	
	@Override
	public void init() {
	}
	
	public void setModuleAndCourse(Module module, Course course) {
		this.module = module;
		this.course = course;
		this.courseTitleLabel.setText(course.getName());
		this.semesterLabel.setText(course.getShortName());
		setImageByUserRole(module.getUserRole());
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
	
	@Override
	public void dispose() {
		
	}

}
