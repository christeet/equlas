package view;

import controller.EqualsController;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import model.EqualsModel;
import model.UserLogin;
import util.IObserver;

public class StudentViewController extends EqualsView implements IObserver<UserLogin> {
	
	private EqualsModel model;
	private EqualsController controller;
	private ObservableList<Data> data;
	
	@FXML
	private Label successPartComplete;
	
	@FXML
	private TableView<Data> table;
	
	@FXML
	private TableColumn<Data, String> courseColumn;
	
	@FXML
	private TableColumn<Data, Number> weightColumn, successColumn;
	
	@FXML 
	private BorderPane container;
	
	@FXML
	protected void onSave() {
//		this.model.addStudent(nameInput.getText(), 
//		Integer.valueOf(yearInput.getText()), 
//		Integer.valueOf(semesterInput.getText()), 
//		Double.valueOf(gradeInput.getText()));
	}
	
	public void setContentView(Parent content) {
		container.setCenter(content);
		//setTitle(I18n.getString("login.title"));
	}
	
	@FXML
	protected void initialize() {
		courseColumn.setCellValueFactory(d -> d.getValue().courseProperty());
		weightColumn.setCellValueFactory(d -> d.getValue().weightProperty());
		successColumn.setCellValueFactory(d -> d.getValue().successProperty());
		
	  this.data = FXCollections.observableArrayList();
	}
	
	public void init(EqualsModel model, EqualsController controller) {
		this.model = model;
		this.controller = controller;
		this.model.getUserLogin().addObserver(this);
		
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
	public void update(UserLogin userLogin, Object arg) {
//		Boolean isNew = true;
//		Model.Student student = (Model.Student) arg;
//		for(Data d : this.data) {
//			System.out.println("DataEditor: " + student.getName() + "\nObserver Student: " + d.getName());
//			if(d.getName().equals(student.getName())) {
//				Platform.runLater(() -> {
//					d.setGrade((int) student.getGrade());
//				});
//				isNew = false;
//				break;
//			}
//		}
//		if(isNew) {
//			Data dat = new Data(
//				student.getName(), 
//				this.model.getYearOfBirth(student.getName()), 
//				this.model.getSemester(student.getName()), 
//			  this.model.getGrade(student.getName())
//			);
//			Platform.runLater(() -> {
//				this.data.add(dat);
//				table.setItems(this.data);
//			});
//		}
	}
	
  private static class Data {
    private final StringProperty course;
    private final DoubleProperty weight;
    private final DoubleProperty success;

    private Data(String course, double weight, int success) {
        this.course = new SimpleStringProperty(course);
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
