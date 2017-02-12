package view;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class CasResponsibleViewController extends EqualsView {
	
	private ObservableList<Data> data;
	
	@FXML
	private TableView<Data> table;
	
	@FXML
	private TableColumn<Data, String> studentColumn;
	
	@FXML
	protected void onSave() {
		this.model.getClass();
	}
	
	@FXML
	protected void initialize() {
		studentColumn.setCellValueFactory(d -> d.getValue().courseProperty());
		
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
    private final StringProperty student;
    private final DoubleProperty weight;
    private final DoubleProperty success;

    private Data(String student, double weight, int success) {
        this.student = new SimpleStringProperty(student);
        this.weight = new SimpleDoubleProperty(weight);
        this.success = new SimpleDoubleProperty(success);
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

    private double getSuccess() {
    	return this.success.get();
    }
    
    private DoubleProperty successProperty() { 
    	return this.success;
    }
  }

  
}
