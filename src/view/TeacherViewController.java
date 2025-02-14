package view;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import data.Course;
import data.Person;
import data.Rating;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Region;
import javafx.util.StringConverter;
import resources.I18n;

public class TeacherViewController extends EqualsView {

	@FXML private Label courseLabel;
	@FXML private Label changesLabel;
	@FXML private Button saveButton;
	@FXML private TableView<Data> table;
	@FXML private TableColumn<Data, String> studentColumn;
	@FXML private TableColumn<Data, Number> successColumn;
	

	private ObservableList<Data> data;
	private Course currentCourse;
	private ObservableMap<Integer, Integer> successRateChanges = FXCollections.observableHashMap();
	
	@FXML
	protected void onSave() {
		int noSaveSuccess = 0;
		Iterator<Entry<Integer, Integer>> iterator = successRateChanges.entrySet().iterator();
		while(iterator.hasNext()) {
			Map.Entry<Integer, Integer> change = (Map.Entry<Integer, Integer>)iterator.next();
			if(!setNewSuccessRate(change.getKey(), change.getValue())) {
				noSaveSuccess++;
			}
			iterator.remove();
		}
		if(noSaveSuccess > 0) {
			showNoSaveSuccessAlert(noSaveSuccess);
		}
	}
	
	private void showNoSaveSuccessAlert(int noSaveSuccess) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle(I18n.getString("alert.warning"));
		alert.setHeaderText(null);
		alert.setContentText(String.format(I18n.getString("alert.content.noSaveSuccess"), noSaveSuccess));
		alert.getDialogPane().getChildren().stream()
			.filter(node -> node instanceof Label)
			.forEach(node -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
		alert.show();
	}
	
	@FXML
	protected void initialize() {
		table.setEditable(true);
		studentColumn.setCellValueFactory(d -> d.getValue().studentNameProperty());
		successColumn.setCellValueFactory(d -> d.getValue().successProperty());
		studentColumn.setSortable(true);
		studentColumn.setSortType(SortType.ASCENDING);
		data = table.getItems();
		saveButton.disableProperty().bind(Bindings.isEmpty(successRateChanges));
		
		changesLabel.visibleProperty().bind(Bindings.isEmpty(successRateChanges).not());
		successRateChanges.addListener((MapChangeListener.Change<? extends Integer, ? extends Integer> c) -> {
			changesLabel.setText(String.format(I18n.getString("view.changes"), successRateChanges.size()));
		});
		
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
					return -2; // non-valid input
				} 
	        }
	    }));
		successColumn.setOnEditCommit((CellEditEvent<Data, Number> t) -> {
			int newValue = t.getNewValue().intValue();
			if(newValue <= 100 && newValue >= -1) { 
				addSuccessRateChange(t.getRowValue(),newValue);
				t.getRowValue().successProperty().set(newValue);
			}
			table.refresh();
		});

        table.setFixedCellSize(25);
        table.prefHeightProperty().bind(table.fixedCellSizeProperty().multiply(Bindings.size(table.getItems()).add(2.01)));
        table.minHeightProperty().bind(table.prefHeightProperty());
        table.maxHeightProperty().bind(table.prefHeightProperty());
	}

    private void addSuccessRateChange(Data data, int newSuccessRate) {
    	System.out.println("adding data");
    	successRateChanges.put(data.getStudent().getId(), newSuccessRate);
	}
    
    private boolean setNewSuccessRate(int studentId, int newSuccessRate) {
    	if(newSuccessRate == -1) {
        	return controller.removeRating(studentId, currentCourse);
    	} else {
    		return controller.setNewSuccessRate(studentId, currentCourse, newSuccessRate);
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
