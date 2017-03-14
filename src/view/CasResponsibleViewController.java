package view;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;

import data.Course;
import data.Module;
import data.Person;
import data.Rating;
import data.UserRole;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
		// TODO: IMPLEMENT!!!!!!!
	}
	
	@FXML
	protected void initialize() {
		table.setEditable(true);
		studentColumn.setCellValueFactory(d -> d.getValue().getStudentNameProperty());
		studentColumn.setSortable(true);
		studentColumn.setSortType(SortType.ASCENDING);
		this.data = table.getItems();
	}
	
	
	@Override
	public void init() {
        module = model.getContextModule();
		Platform.runLater(() -> {
			String text = String.format("%s %s", I18n.getString("view.module"), module.getName());
			casTitleLabel.setText(text);
		});
        setTableColumns();
        
        /* update table-data as soon as the ratings-List changes (used for optimistic locking) */
        model.getRatingListProperty().addListener((ListChangeListener.Change<? extends Rating> c) -> {
	        setTableData();
    	});
        setTableData();
        
        table.setFixedCellSize(25);
        table.prefHeightProperty().bind(table.fixedCellSizeProperty().multiply(Bindings.size(table.getItems()).add(2.01)));
        table.minHeightProperty().bind(table.prefHeightProperty());
        table.maxHeightProperty().bind(table.prefHeightProperty());
    }
	
	private void setTableColumns() {
		UserRole userRole = module.getUserRole();
		final int teacherId = model.getUserLogin().getUser().getId();
		ArrayList<TableColumn<Data, Number>> tableColumns = new ArrayList<>();
		FilteredList<Course> courses = model.getCoursesListProperty().filtered(c -> {
			return c.getModuleId() == module.getId()
					&& ((userRole==UserRole.TEACHER) ? (c.getTeacherId() == teacherId) : (true));
			});
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
	}
	
	private void setTableData() {
		data.clear();
        for(Person student : model.getStudentListProperty()) {
    		ObservableList<Rating> ratingList = model.getRatingListProperty()
    				.filtered(r -> r.getStudentId() == student.getId());
    		
    		Data row = new Data(student, ratingList);
    		data.add(row);
        }
		table.getSortOrder().add(studentColumn);
	}

    private void setNewSuccessRate(Course course, Data data, int newSuccessRate) {
    	System.out.format("Setting new rating %d for course %s\r\n", newSuccessRate, course.getShortName());
    	if(newSuccessRate == -1) {
        	controller.removeRating(data.getStudent().getId(), course);
    	} else {
    		controller.setNewSuccessRate(data.getStudent().getId(), course, newSuccessRate);
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
