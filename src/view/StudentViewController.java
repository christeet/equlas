package view;

import data.Course;
import data.Module;
import data.Rating;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import resources.I18n;
import javafx.scene.control.TableView;

public class StudentViewController extends EqualsView {
	
	private ObservableList<Data> data;
	private Module contextModule;
	private int studentId;

	@FXML private Label casTitleLabel;
	@FXML private Label successPartComplete;
	@FXML private TableView<Data> table;
	@FXML private TableColumn<Data, String> courseColumn;
	@FXML private TableColumn<Data, String> weightColumn;
	@FXML private TableColumn<Data, String> successColumn;
	
	@FXML private TableView<String> totalTable;
	@FXML private TableColumn<String, String> titleColumn;
	@FXML private TableColumn<String, String> emptyColumn;
	@FXML private TableColumn<String, String> totalSuccessColumn;
	
	
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
        table.setFixedCellSize(25);
        table.prefHeightProperty().bind(table.fixedCellSizeProperty().multiply(Bindings.size(table.getItems()).add(2.01)));
        table.minHeightProperty().bind(table.prefHeightProperty());
        table.maxHeightProperty().bind(table.prefHeightProperty());
        totalTable.setFixedCellSize(25);
        totalTable.prefHeightProperty().bind(totalTable.fixedCellSizeProperty().multiply(0));
        totalTable.minHeightProperty().bind(totalTable.prefHeightProperty());
        totalTable.maxHeightProperty().bind(totalTable.prefHeightProperty());
        courseColumn.setSortable(true);
        courseColumn.setSortType(SortType.ASCENDING);
        titleColumn.setSortable(false);
        emptyColumn.setSortable(false);
        totalSuccessColumn.setSortable(false);
        titleColumn.prefWidthProperty().bind(courseColumn.widthProperty());
        emptyColumn.prefWidthProperty().bind(weightColumn.widthProperty());
        totalSuccessColumn.prefWidthProperty().bind(successColumn.widthProperty());
		this.data = table.getItems();
	}
	
	@Override
	public void init() {
		studentId = model.getUserLogin().getUser().getId();
		contextModule = model.getContextModule();
		Platform.runLater(() -> {
			String text = String.format("%s %s", I18n.getString("view.module"), contextModule.getName());
			casTitleLabel.setText(text);
		});
		
		System.out.format("ContextModule is %s (Nr %d)\r\n", contextModule.getShortName(), contextModule.getId());
		
		double accumulatedSuccess = 0;
		double currentWeight = 0;
		double totalWeight = 0;
		
		for(Course course : model.getCoursesListProperty().filtered(p -> p.getModuleId() == contextModule.getId())) {
			Integer success = null;
			try {
				Rating rating = model.getRatingListProperty()
						.filtered(r -> r.getCourseId() == course.getId() 
									&& r.getStudentId() == studentId)
						.get(0);
				success = rating.getSuccessRate();
				accumulatedSuccess += success * course.getWeight();
				currentWeight += course.getWeight();
			} catch (IndexOutOfBoundsException | NullPointerException e){}

			totalWeight += course.getWeight();
			Data d = new Data(course, success);
			this.data.add(d);
		}
		table.getSortOrder().add(courseColumn);
		
		totalSuccessColumn.setText(String.format("%d / %d",
				(int)Math.round(accumulatedSuccess/currentWeight),
				(int)Math.round(accumulatedSuccess/totalWeight)));
	}
	
	@Override
	public void dispose() {
		
	}
	
  private static class Data {
    private final StringProperty courseName;
    private final StringProperty weight;
    private final StringProperty success;

    private Data(Course course, Integer success) {
        this.courseName = new SimpleStringProperty(course.getName());
        this.weight = new SimpleStringProperty(String.format("%.1f", course.getWeight()));
        if(success != null) {
        	this.success = new SimpleStringProperty(String.format("%d", success));
        } else {
        	this.success = new SimpleStringProperty("");
        }
    }
    
    private StringProperty courseNameProperty() {
    	return this.courseName;
    }
    
    private StringProperty weightProperty() {
    	return this.weight;
    }
    
    private StringProperty successProperty() { 
    	return this.success;
    }
  }

  
}
