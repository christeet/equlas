package view;

import java.util.List;

import data.Course;
import data.Module;
import data.Person;
import data.Rating;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class CasResponsibleViewController extends EqualsView {

	private ObservableList<Course> courses;
	private ObservableList<Rating> ratings;
	private ObservableList<Data> data;
	private Module module;

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
		studentColumn.setCellValueFactory(d -> d.getValue().studentNameProperty());
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
		module = model.getContextModule();
		casTitleLabel.setText(module.getName());
		courses = model.getCoursesListProperty().filtered(c -> c.getModuleId() == module.getId());

		TableColumn<Data, Number> [] tableColumns = new TableColumn[courses.size()];    

        int columnIndex = 0;
		for(Course c : courses) {
			TableColumn<Data, Number> courseColumn = new TableColumn<Data, Number>(c.getShortName());
			final int index = columnIndex;
            courseColumn.setCellValueFactory(d -> d.getValue().ratingsProperty(index));
            /*courseColumn.setCellFactory(TextFieldTableCell.<Data, Number>forTableColumn(new StringConverter<Number>() {
    	        private final NumberFormat nf = NumberFormat.getNumberInstance();
    	        
    	        {
    	             nf.setMaximumFractionDigits(1);
    	             nf.setMinimumFractionDigits(1);
    	        }

    	        @Override public String toString(final Number value) {
    	        	if(value.intValue() == -1) {
    	        		return "";
    	        	}
    	            return nf.format(value);
    	        }

    	        @Override public Number fromString(final String s) {
    	        	if(s.isEmpty()) {
    	        		return -1;
    	        	}
    	            try {
    					return nf.parse(s);
    				} catch (ParseException e) {
    					e.printStackTrace();
    					return -1;
    				} 
    	        }
    	    }));*/
            tableColumns[columnIndex++] = courseColumn;
		}
        table.getColumns().addAll(tableColumns);
        
        
        for(Person student : model.getStudentListProperty()) {
    		List<Rating> ratingList = model.getRatingListProperty().filtered(r -> r.getStudentId() == student.getId());
    		
    		Data row = new Data(student, courses, ratingList);
    		table.getItems().add(row);
        }
	}
	
	@Override
	public void dispose() {
		
	}
	
	

	  private static class Data {
		private final Person student;
	    private final StringProperty studentName;
	    private ListProperty<Integer> ratings;
	    private ListProperty<Course> courses;

	    private Data(Person student, List<Course> courses, List<Rating> ratings) {
	    	this.student = student;
	        this.studentName = new SimpleStringProperty(student.getName());
	        ObservableList<Course> observableCoursesList = FXCollections.observableArrayList(courses);
	        this.courses = new SimpleListProperty<Course>(observableCoursesList);

	        ObservableList<Integer> observableRatingsList = FXCollections.observableArrayList();
	        for(Rating r : ratings) {
	        	observableRatingsList.add(r.getSuccessRate());
	        }
	        this.ratings = new SimpleListProperty<Integer>(observableRatingsList);
			//System.out.format("Student %s with rating %d\r\n", student.getName(), success);
	    }

	    private Person getStudent() { 
	    	return this.student; 
	    }
	    
	    private StringProperty studentNameProperty() {
	    	return this.studentName;
	    }
	    public IntegerProperty ratingsProperty(int index) { 
	    	return new SimpleIntegerProperty(ratings.get(index));
	    }
	    
	    private ListProperty<Course> coursesProperty() { 
	    	return this.courses;
	    }
	  }

  
}
