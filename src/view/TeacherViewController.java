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
	private TableColumn<Data, String> courseColumn;
	
	@FXML
	private TableColumn<Data, Number> weightColumn;
	
	@FXML
	protected void onSave() {
		this.model.getClass();
	}
	
	@FXML
	protected void initialize() {
		courseColumn.setCellValueFactory(d -> d.getValue().courseProperty());
		weightColumn.setCellValueFactory(d -> d.getValue().weightProperty());
		
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
    private final StringProperty course;
    private final DoubleProperty weight;

    private Data(String course, double weight) {
        this.course = new SimpleStringProperty(course);
        this.weight = new SimpleDoubleProperty(weight);
    }

    private String getCourse() { 
    	return this.course.get(); 
    }
    
    private StringProperty courseProperty() {
    	return this.course;
    }
    
    private double getWeight() {
    	return this.weight.get();
    }
    
    private DoubleProperty weightProperty() {
    	return this.weight;
    }
  }

  
}
