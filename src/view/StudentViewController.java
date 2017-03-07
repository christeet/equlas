package view;

import java.text.NumberFormat;
import java.text.ParseException;

import data.Course;
import data.Module;
import data.Rating;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;

public class StudentViewController extends EqualsView {
	
	private ObservableList<Data> data;
	
	@FXML
	private Label successPartComplete;
	
	@FXML
	private TableView<Data> table;
	
	@FXML
	private TableColumn<Data, String> courseColumn;

	@FXML
	private TableColumn<Data, String> weightColumn;
	
	@FXML
	private TableColumn<Data, Number> successColumn;
	
	private Module contextModule;
	private int studentId;
	
	@FXML
	protected void onSave() {
		this.model.getClass();
	}
	
	@FXML
	protected void initialize() {
		// table.setEditable(true); // ha ha, schön wär's...
		courseColumn.setCellValueFactory(d -> d.getValue().courseNameProperty());
		weightColumn.setCellValueFactory(d -> d.getValue().weightProperty());
		successColumn.setCellValueFactory(d -> d.getValue().successProperty());
		this.data = table.getItems();
		
		
		successColumn.setCellFactory(TextFieldTableCell.<Data, Number>forTableColumn(new StringConverter<Number>() {
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
	    }));
		successColumn.setOnEditCommit((CellEditEvent<Data, Number> t) -> {
			setNewSuccessRate(t.getRowValue(), t.getNewValue().intValue());
			t.getRowValue().successProperty().set(t.getNewValue().intValue());
		});
	}
	
	@Override
	public void init() {
		studentId = model.getUserLogin().getUser().getId();
		contextModule = model.getContextModule();
		System.out.format("ContextModule is %s (Nr %d)\r\n", contextModule.getShortName(), contextModule.getId());
		for(Course course : model.getCoursesListProperty().filtered(p -> p.getModuleId() == contextModule.getId())) {
			Integer success = null;
			try {
				Rating rating = model.getRatingListProperty()
						.filtered(r -> r.getCourseId() == course.getId() 
									&& r.getStudentId() == studentId)
						.get(0);
				success = rating.getSuccessRate();
			} catch (IndexOutOfBoundsException | NullPointerException e){}
			
			Data d = new Data(course, success);
			this.data.add(d);
		}
	}
	
	@Override
	public void dispose() {
		
	}
	
    private void setNewSuccessRate(Data data, int newSuccessRate) {
    	if(newSuccessRate == -1) {
        	controller.removeRating(studentId, data.getCourse().getId());
    	} else {
    		controller.setNewSuccessRate(studentId, data.getCourse().getId(), newSuccessRate);
    	}
    }
	
	
  private static class Data {
	private final Course course;
    private final StringProperty courseName;
    private final StringProperty weight;
    private final IntegerProperty success;

    private Data(Course course, Integer success) {
    	this.course = course;
        this.courseName = new SimpleStringProperty(course.getName());
        this.weight = new SimpleStringProperty(String.format("%.1f", course.getWeight()));
        if(success == null) { success = -1; }
        this.success = new SimpleIntegerProperty(success);
    }

    private Course getCourse() { 
    	return this.course; 
    }
    
    private StringProperty courseNameProperty() {
    	return this.courseName;
    }
    
    private StringProperty weightProperty() {
    	return this.weight;
    }
    
    private IntegerProperty successProperty() { 
    	return this.success;
    }
  }

  
}
