package view;

import data.Course;
import data.Module;
import data.UserRole;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class CourseCellViewController extends EqualsView {

	@FXML private ImageView userRoleIcon;
	@FXML private Label courseTitleLabel;
	@FXML private Label semesterLabel;

	private ListCell<Course> containingCell;
	private Module module;
	private Course course;
	
	@FXML
	protected void initialize() {
	}
	
	@Override
	public void init() {
	}
	
	public void setModuleAndCourse(ListCell<Course> cell, Module module, Course course) {
		this.containingCell = cell;
		this.module = module;
		this.course = course;
		this.courseTitleLabel.setText(course.getName());
		this.semesterLabel.setText(course.getShortName());
		setImageByUserRole(module.getUserRole());
	}
	
	public Course getCourse() {
		return course;
	}
	
	private void setImageByUserRole(UserRole userRole) {
		if(course.getTeacherId() == model.getUserLogin().getUser().getId()) {
			userRole = UserRole.TEACHER;
		}
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
	
	public void selectCell() {
		System.out.format("Selecting Cell %s of Module %s\r\n",course.getShortName(), module.getShortName());
		containingCell.getListView().requestFocus();
		containingCell.getListView().getSelectionModel().clearSelection();
		containingCell.getListView().getSelectionModel().select(this.course);
	}
	
	@Override
	public void dispose() {
		
	}

}
