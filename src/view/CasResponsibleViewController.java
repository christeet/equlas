package view;

import data.Person;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class CasResponsibleViewController extends EqualsView {
	
	private ObservableList<Data> data;

	@FXML private Label casTitleLabel;
	@FXML private Label successPartComplete;
	@FXML private TableView<Data> table;
	@FXML private TableColumn<Data, String> studentColumn;
	@FXML private Button saveButton;
	
	@FXML
	protected void onSave() {
		this.model.getClass();
	}
	
	@FXML
	protected void initialize() {
		//studentColumn.setCellValueFactory(d -> d.getValue().courseProperty());
		
	  //this.data = FXCollections.observableArrayList();
	}
	
	private void getCoursesFromModule() {
		
	}
	
	
	
	@Override
	public void init() {
		Person person = this.model.getUserLogin().getUser();
		System.out.println(
		"FirstName: "
		+ person.getFirstName()
		+ "\nLastName: "
		+ person.getLastName()
		+ "\nName: "
		+ person.getName()
		);
		casTitleLabel.setText(model.getContextModule().getName());
		
//		for(String name : this.model.getStudentName()) {
//			if(name.equals(this.userName())) {
//				for(String course : this.model.getUserCourses())
//				Data d = new Data(course.getName(), this.model.getCourseWeight(course), this.model.getStudentCourseSuccess());
//				this.data.add(d);
//				table.setItems(this.data);
//			}
//		}
	}
	
	@Override
	public void dispose() {
		
	}
	
	
  private static class Data {
    private final StringProperty student;
    private final DoubleProperty success;
		private final StringProperty course;

    private Data(String student, int success, String course) {
        this.student = new SimpleStringProperty(student);
        this.success = new SimpleDoubleProperty(success);
        this.course = new SimpleStringProperty(course);
    }
    
    private String getStudent()  {
    	
    	return student.getName();
    }
    
    private StringProperty studentProperty() {
    	
    	return student;
    }

    private String getCourse() { 
    	return "";//this.course.get(); 
    }
    
    private StringProperty courseProperty() {
    	return null;//this.course;
    }

    private double getSuccess() {
    	return this.success.get();
    }
    
    private DoubleProperty successProperty() { 
    	return this.success;
    }
  }

  
}
