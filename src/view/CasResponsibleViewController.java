package view;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;

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
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;

public class CasResponsibleViewController extends EqualsView {

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
		table.setEditable(true);
		studentColumn.setCellValueFactory(d -> d.getValue().getStudentNameProperty());
		studentColumn.setPrefWidth(200);
		this.data = table.getItems();
	}
	
	
	@Override
	public void init() {
		module = model.getContextModule();
		casTitleLabel.setText(module.getName());

		ArrayList<TableColumn<Data, Number>> tableColumns = new ArrayList<>();
		FilteredList<Course> courses = model.getCoursesListProperty().filtered(c -> c.getModuleId() == module.getId());
		for(Course c : courses) {
			TableColumn<Data, Number> courseColumn = new TableColumn<Data, Number>(c.getShortName());
			courseColumn.setUserData(c);
            courseColumn.setCellValueFactory(d -> d.getValue().getRatingsProperty(c));
            courseColumn.setCellFactory(TextFieldTableCell.<Data, Number>forTableColumn(new StringConverter<Number>() {
    	        private final NumberFormat nf = NumberFormat.getNumberInstance();
    	        
    	        {
    	             nf.setMaximumFractionDigits(0);
    	             nf.setMinimumFractionDigits(0);
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
    	    }));
            courseColumn.setOnEditCommit((CellEditEvent<Data, Number> t) -> {
            	Course currentCourse = (Course)t.getTableColumn().getUserData();
    			setNewSuccessRate(currentCourse, t.getRowValue(), t.getNewValue().intValue());
    			t.getRowValue().getRatingsProperty(currentCourse).set(t.getNewValue().intValue());
    		});
            tableColumns.add(courseColumn);
		}
        table.getColumns().addAll(tableColumns);
        
        
        for(Person student : model.getStudentListProperty()) {
    		ObservableList<Rating> ratingList = model.getRatingListProperty()
    				.filtered(r -> r.getStudentId() == student.getId());
    		
    		Data row = new Data(student, ratingList);
    		data.add(row);
        }
	}

    private void setNewSuccessRate(Course course, Data data, int newSuccessRate) {
    	System.out.format("Setting new rating %d for course %s\r\n", newSuccessRate, course.getShortName());
    	if(newSuccessRate == -1) {
        	controller.removeRating(data.getStudent().getId(), course.getId());
    	} else {
    		controller.setNewSuccessRate(data.getStudent().getId(), course.getId(), newSuccessRate);
    	}
    }
	
	@Override
	public void dispose() {
		
	}
	
	

	  private static class Data {
		private final Person student;
	    private final StringProperty studentName;
	    private ListProperty<Rating> ratings;

	    private Data(Person student, ObservableList<Rating> ratings) {
	    	this.student = student;
	        this.studentName = new SimpleStringProperty(student.getName());
	        this.ratings = new SimpleListProperty<Rating>(ratings);
	    }
	    
	    private Person getStudent() {
	    	return student;
	    }
	    
	    private StringProperty getStudentNameProperty() {
	    	return this.studentName;
	    }
	    
	    public IntegerProperty getRatingsProperty(Course course) { 
	    	for(Rating r : ratings) {
	    		if(r.getCourseId() == course.getId()) {
	    	    	return new SimpleIntegerProperty(r.getSuccessRate());
	    		}
	    	}
	    	return new SimpleIntegerProperty(-1);
	    }
	  }

  
}
