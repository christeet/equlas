package view;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class TeacherViewController extends EqualsView {
	
	private ObservableList<Data> data;
	
	@FXML
	private TableView<Data> table;
	
	@FXML
	private Button saveButton;
	
	@FXML
	private TableColumn<Data, String> studentColumn;
	
	@FXML
	private TableColumn<Data, Number> successColumn;
	
	@FXML
	protected void onSave() {
		this.model.getClass();
	}
	
	@FXML
	protected void initialize() {
		studentColumn.setCellValueFactory(d -> d.getValue().studentNameProperty());
		successColumn.setCellValueFactory(d -> d.getValue().successProperty());
		
	  this.data = FXCollections.observableArrayList();
	}
	
	@Override
	public void init() {
		
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
    private final StringProperty studentName;
    private final DoubleProperty success;

    private Data(String course, double weight) {
        this.studentName = new SimpleStringProperty(course);
        this.success = new SimpleDoubleProperty(weight);
    }

    private String getStudentName() { 
    	return this.studentName.get(); 
    }
    
    private StringProperty studentNameProperty() {
    	return this.studentName;
    }
    
    private double getSuccess() {
    	return this.success.get();
    }
    
    private DoubleProperty successProperty() {
    	return this.success;
    }
  }

  
}
