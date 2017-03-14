package view;

import java.text.NumberFormat;
import java.text.ParseException;

import data.Course;
import data.Person;
import data.Rating;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import resources.I18n;

public class TeacherViewController extends EqualsView {
	
	@FXML private Label courseLabel;
	@FXML private Button saveButton;
	@FXML private TableView<Data> table;
	@FXML private TableColumn<Data, String> studentColumn;
	@FXML private TableColumn<Data, Number> successColumn;

	private ObservableList<Data> data;
	private Course currentCourse;
	
	@FXML
	protected void onSave() {
		this.model.getClass();
	}
	
	@FXML
	protected void initialize() {
		table.setEditable(true);
		studentColumn.setCellValueFactory(d -> d.getValue().studentNameProperty());
		successColumn.setCellValueFactory(d -> d.getValue().successProperty());
		studentColumn.setSortable(true);
		studentColumn.setSortType(SortType.ASCENDING);
		this.data = table.getItems();
		
		
		successColumn.setCellFactory(TextFieldTableCell.<Data, Number>forTableColumn(new StringConverter<Number>() {
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
		successColumn.setOnEditCommit((CellEditEvent<Data, Number> t) -> {
			setNewSuccessRate(t.getRowValue(), t.getNewValue().intValue());
			t.getRowValue().successProperty().set(t.getNewValue().intValue());
		});

        
        table.setFixedCellSize(25);
        table.prefHeightProperty().bind(table.fixedCellSizeProperty().multiply(Bindings.size(table.getItems()).add(2.01)));
        table.minHeightProperty().bind(table.prefHeightProperty());
        table.maxHeightProperty().bind(table.prefHeightProperty());
	}
	
    private void setNewSuccessRate(Data data, int newSuccessRate) {
    	if(newSuccessRate == -1) {
        	controller.removeRating(data.getStudent().getId(), currentCourse);
    	} else {
    		controller.setNewSuccessRate(data.getStudent().getId(), currentCourse, newSuccessRate);
    	}
    }
	
	
	@Override
	public void init() {
	}
	
	public void setCourse(Course currentCourse) {
		this.currentCourse = currentCourse;
		
		Platform.runLater(() -> {
			String text = String.format("%s %s", I18n.getString("view.course"), this.currentCourse.getName());
			courseLabel.setText(text);
		});
		
        /* update table-data as soon as the ratings-List changes (used for optimistic locking) */
        model.getRatingListProperty().addListener((ListChangeListener.Change<? extends Rating> c) -> {
	        setTableData();
    	});
        setTableData();
	}
	
	private void setTableData() {
		this.data.clear();
		for(Person student : model.getStudentListProperty()) {
			Integer success = null;
			try {
				Rating rating = model.getRatingListProperty()
						.filtered(r -> r.getCourseId() == this.currentCourse.getId() 
									&& r.getStudentId() == student.getId())
						.get(0);
				success = rating.getSuccessRate();
			} catch (IndexOutOfBoundsException | NullPointerException e){}
			
			Data d = new Data(student, success);
			this.data.add(d);
		}
		table.getSortOrder().add(studentColumn);
	}
	
	@Override
	public void dispose() {
		
	}
	

	
	  private static class Data {
		private final Person student;
	    private final StringProperty studentName;
	    private final IntegerProperty success;

	    private Data(Person student, Integer success) {
	    	this.student = student;
	        this.studentName = new SimpleStringProperty(student.getName());
	        if(success == null) { success = -1; }
	        this.success = new SimpleIntegerProperty(success);

			//System.out.format("Student %s with rating %d\r\n", student.getName(), success);
	    }

	    private Person getStudent() { 
	    	return this.student; 
	    }
	    
	    private StringProperty studentNameProperty() {
	    	return this.studentName;
	    }
	    
	    private IntegerProperty successProperty() { 
	    	return this.success;
	    }
	  }

  
}
