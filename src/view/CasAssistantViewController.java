package view;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
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
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import xml.GenerateXML;

public class CasAssistantViewController extends EqualsView {

	private ObservableList<Data> data;
	private Module module;
	private ArrayList<Module> moduleList;
	private ArrayList<Person> students;

	@FXML
	private Label casTitleLabel;
	@FXML
	private TableView<Data> table;
	@FXML
	private TableColumn<Data, String> studentColumn;
	@FXML
	private Button printButton;
	private File file;

	@FXML
	protected void onPrint() {
		try {
			file = directoryChooser();			
			makeXMLLeistungsnachweis(file);
			evaluateStudentsForCertificate();
			makeXMLCertificate(file);
			openPDFs();
		} catch (Exception e) {
			System.out.println("XML Generating failed! " + e.getMessage());
		}
	}
	
	private File directoryChooser() {
		Parent root = getRootNode();
		Stage stage = (Stage)root.getScene().getWindow();
		
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Directory");
		chooser.setInitialDirectory(new File("resources/output/"));
		return chooser.showDialog(stage);
		
	}
	
	private void openPDFs() throws Exception {
		if (Desktop.isDesktopSupported()) {
		    try {
		        File fileLeistungsnachweis = new File("resources/output/fertigLeistungsnachweis.pdf");
		        File fileCertificate = new File("resources/output/fertigCertificate.pdf");
		        Desktop.getDesktop().open(fileLeistungsnachweis);
		        Desktop.getDesktop().open(fileCertificate);
		    } catch (IOException ex) {
		    	throw new Exception("No Application found to open PDF! " + ex.getMessage());
		    }
		}
	}

	private void evaluateStudentsForCertificate() throws Exception {
		try {
		ObservableList<Person> studentList = model.getStudentListProperty();
		for(Person student : studentList) {
			int summe = 0;
			int countCourse = 0;
			ObservableList<Course> studentCourses = model.getCoursesListProperty()
					.filtered(f -> f.getModuleId() == module.getId());
			for(Course c : studentCourses) {
				ObservableList<Rating> ratingStudentList = model.getRatingListProperty()
						.filtered(r -> r.getStudentId() == student.getId() && r.getCourseId() == c.getId());
				for(Rating r : ratingStudentList) {
					System.out.println("RatingStudentList: " + r.getSuccessRate());
					summe = calculateGrade(r.getSuccessRate(), summe);
					countCourse++;
				}
				System.out.println("RatingsSize: " + ratingStudentList.size());
				
			}
			if((summe / countCourse) >= 50) {
				System.out.println("EvaluateRating: " + (summe / countCourse));
				this.students.add(student);
			}
		}
		} catch (Exception e) {
			throw new Exception("Could not evaluate Students Rating: " + e.getMessage());
		}
	}
	
	private int calculateGrade(int rate, int sum) {
		return sum + rate;
	}

	private void makeXMLLeistungsnachweis(File file) {
		try {
			GenerateXML gxl = new GenerateXML(module);
			gxl.makeXMLDocument();
			System.out.println("ModuleName: " + module.getName());
			generatePDFLeistungsnachweis();
		} catch (Exception eg) {
			System.out.println("Could not generate Leistungsnachweis XML! Reason: " + eg.getMessage());
		}
	}

	private void makeXMLCertificate(File file) {
		try {
			System.out.println("Students: " + students.toString() + "\nModule: " + module.getShortName());
			moduleList.add(module);
			GenerateXML gxc = new GenerateXML(students, moduleList);
			System.out.println("GXC: " + gxc);
			gxc.makeXMLDocument();
			System.out.println("ModuleName: " + module.getName());
			generatePDFCertificate();
		} catch (Exception eg) {
			eg.printStackTrace();
			System.out.println("Could not generate Certificate XML! Reason: " + eg.getMessage());
		}
	}

	private void generatePDFLeistungsnachweis() {
		try {
			PrintManagerLeistungsnachweis pml = new PrintManagerLeistungsnachweis();
			pml.generateXMLDocument();
		} catch (Exception el) {
			System.out.println("Could not Print Leistungsnachweis, because of: " + el.getMessage());
		}
	}

	private void generatePDFCertificate() {
		try {
			PrintManagerCertificate pmc = new PrintManagerCertificate();
			pmc.generateXMLDocument();
		} catch (Exception ec) {
			System.out.println("Could not Print Leistungsnachweis, because of: " + ec.getMessage());
		}
	}

	@FXML
	protected void initialize() {
		students = new ArrayList<Person>();
		moduleList = new ArrayList<Module>();
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
