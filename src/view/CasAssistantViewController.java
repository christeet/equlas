package view;

import java.util.ArrayList;

import data.Course;
import data.Module;
import data.Person;
import data.Rating;
import data.UserRole;
import equals.PrintManagerCertificate;
import equals.PrintManagerLeistungsnachweis;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import xml.GenerateXML;

public class CasAssistantViewController extends EqualsView {

	private ObservableList<Data> data;
	private Module module;
	private ArrayList<Person> students;

	@FXML
	private Label casTitleLabel;
	// @FXML private Label successPartComplete;
	@FXML
	private TableView<Data> table;
	@FXML
	private TableColumn<Data, String> studentColumn;
	@FXML
	private Button printButton;

	@FXML
	protected void onPrint() {
		makeXMLLeistungsnachweis();
		generateXMLLeistungsnachweis();
		evaluateStudentsForCertificate();
		makeXMLCertificate();
		generateXMLCertificate();
	}

	private void evaluateStudentsForCertificate() {
		int summe = 0;
		ObservableList<Person> studentList = model.getStudentListProperty();
		for(Person student : studentList) {
			ObservableList<Rating> ratingStudentList = model.getRatingListProperty()
					.filtered(r -> r.getStudentId() == student.getId());
			for(Rating r : ratingStudentList) {
				summe += r.getSuccessRate();
			}
			if(summe >= 50) {
				students.add(student);
			}
		}
	}

	private void makeXMLLeistungsnachweis() {
		try {
			GenerateXML gxl = new GenerateXML(module);
			gxl.makeXMLDocument();
		} catch (Exception eg) {
			System.out.println("Could not generate XML! Reason: " + eg.getMessage());
		}
	}

	private void makeXMLCertificate() {
		try {
			GenerateXML gxc = new GenerateXML(students, module);
			gxc.makeXMLDocument();
		} catch (Exception eg) {
			System.out.println("Could not generate XML! Reason: " + eg.getMessage());
		}
	}

	private void generateXMLLeistungsnachweis() {
		try {
			PrintManagerLeistungsnachweis pml = new PrintManagerLeistungsnachweis();
			pml.generateXMLDocument();
		} catch (Exception el) {
			System.out.println("Could not Print Leistungsnachweis, because of: " + el.getMessage());
		}
	}

	private void generateXMLCertificate() {
		try {
			PrintManagerCertificate pmc = new PrintManagerCertificate();
			pmc.generateXMLDocument();
		} catch (Exception ec) {
			System.out.println("Could not Print Leistungsnachweis, because of: " + ec.getMessage());
		}
	}

	@FXML
	protected void initialize() {
		studentColumn.setCellValueFactory(d -> d.getValue().getStudentNameProperty());
		studentColumn.setSortable(true);
		studentColumn.setSortType(SortType.ASCENDING);
		this.data = table.getItems();
	}

	@Override
	public void init() {
		module = model.getContextModule();
		casTitleLabel.setText(module.getName());
		UserRole userRole = module.getUserRole();
		final int teacherId = model.getUserLogin().getUser().getId();

		ArrayList<TableColumn<Data, String>> tableColumns = new ArrayList<>();
		FilteredList<Course> courses = model.getCoursesListProperty().filtered(c -> {
			return c.getModuleId() == module.getId()
					&& ((userRole == UserRole.TEACHER) ? (c.getTeacherId() == teacherId) : (true));
		});
		for (Course c : courses) {
			TableColumn<Data, String> courseColumn = new TableColumn<Data, String>();
			Label courseHeader = new Label(c.getShortName());
			courseHeader.setTooltip(new Tooltip(c.getName()));
			courseColumn.setGraphic(courseHeader);
			courseColumn.setUserData(c);
			courseColumn.setCellValueFactory(d -> d.getValue().getRatingsProperty(c));
			courseColumn.setPrefWidth(100);
			tableColumns.add(courseColumn);
		}
		table.getColumns().addAll(tableColumns);

		 for (Person student : model.getStudentListProperty()) {
			ObservableList<Rating> ratingList = model.getRatingListProperty()
					.filtered(r -> r.getStudentId() == student.getId());

			Data row = new Data(student, ratingList);
			data.add(row);
		}

		table.getSortOrder().add(studentColumn);

		table.setFixedCellSize(25);
		table.prefHeightProperty()
				.bind(table.fixedCellSizeProperty().multiply(Bindings.size(table.getItems()).add(2.01)));
		table.minHeightProperty().bind(table.prefHeightProperty());
		table.maxHeightProperty().bind(table.prefHeightProperty());

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

		public StringProperty getRatingsProperty(Course course) {
			for (Rating r : ratings) {
				if (r.getCourseId() == course.getId()) {
					return new SimpleStringProperty(String.format("%d", r.getSuccessRate()));
				}
			}
			return new SimpleStringProperty("");
		}
	}

}
