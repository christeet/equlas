package view;

import java.awt.Desktop;
import java.io.File;
import java.util.ArrayList;

import data.Course;
import data.Module;
import data.Person;
import data.Rating;
import data.UserRole;
import equals.PrintManagerCertificate;
import equals.PrintManagerLeistungsnachweis;
import javafx.application.Platform;
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
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import resources.I18n;
import util.Prefs;
import xml.GenerateXML;

public class CasAssistantViewController extends EqualsView {

	private ObservableList<Data> data;
	private Module module;
	private ArrayList<Module> moduleList;
	private ArrayList<Person> students;
	private File userPDFPath;


	@FXML
	private Label casTitleLabel;
	@FXML
	private TableView<Data> table;
	@FXML
	private TableColumn<Data, String> studentColumn;
	@FXML
	private Button printButton;

	@FXML
	protected void onPrint() {
		printButton.setDisable(true);
		try {
			userPDFPath = directoryChooser(Prefs.get().getOutputPath());	
			Prefs.get().setOutputPath(userPDFPath.getAbsolutePath());
			Prefs.save();
			makeXMLLeistungsnachweis(userPDFPath);
			evaluateStudentsForCertificate();
			
			makeXMLCertificate(userPDFPath);
			openPDFs();
		} catch (Exception e) {
			System.out.println("XML Generating failed! " + e.getMessage());
		}
		finally {
			printButton.setDisable(false);
		}
	}
	
	private File directoryChooser(String initialPath) {
		Parent root = getRootNode();
		Stage stage = (Stage)root.getScene().getWindow();
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Directory");
		chooser.setInitialDirectory(new File(initialPath));
		return chooser.showDialog(stage);
	}
	
	private void openPDFs() throws Exception {
		if (Desktop.isDesktopSupported()) {
			new Thread(() -> {
			    try {
			        File fileLeistungsnachweis = new File(userPDFPath + "/fertigLeistungsnachweis.pdf");
			        File fileCertificate = new File(userPDFPath + "/fertigCertificate.pdf");
			        Desktop.getDesktop().open(fileLeistungsnachweis);
			        Desktop.getDesktop().open(fileCertificate);
			    } catch (Exception ex) {
			    	System.out.println("No Application found to open PDF! " 
			    			+ ex.getMessage() + "\n" + ex.getStackTrace());
			    }
			}).start();
		}
	}

	private void evaluateStudentsForCertificate() throws Exception {
		try {
			ObservableList<Person> studentList = model.getStudentsWithGoodGradesProperty();
			for(Person student : studentList) {
				int summe = 0;
				ObservableList<Course> studentCourses = model.getCoursesListProperty()
						.filtered(f -> f.getModuleId() == module.getId());
				for(Course c : studentCourses) {
					ObservableList<Rating> ratingStudentList = model.getRatingListProperty()
							.filtered(r -> r.getStudentId() == student.getId() 
										&& r.getCourseId() == c.getId());
					for(Rating r : ratingStudentList) {
						System.out.println("RatingStudentList: " + r.getSuccessRate());
						summe = calculateGrade(r.getSuccessRate(), summe);
					}
					System.out.println("RatingsSize: " + ratingStudentList.size());
				}
				this.students.add(student);
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
			GenerateXML gxl = new GenerateXML(model);
			gxl.makeXMLDocumentForLeistungsnachweis();
			System.out.println("ModuleName: " + module.getName());
			generatePDFLeistungsnachweis(file);
		} catch (Exception eg) {
			System.out.println("Could not generate Leistungsnachweis XML! Reason: " + eg.getMessage());
		}
	}

	private void makeXMLCertificate(File file) {
		try {
			System.out.println("Students: " + students.toString() + "\nModule: " + module.getShortName());
			moduleList.add(module);
			GenerateXML gxc = new GenerateXML(model);
			System.out.println("GXC: " + gxc);
			gxc.makeXMLDocumentForZertifikat();
			System.out.println("ModuleName: " + module.getName());
			generatePDFCertificate(file);
		} catch (Exception eg) {
			System.out.println("Could not generate Certificate XML! Reason: " + eg.getMessage());
		}
	}

	private void generatePDFLeistungsnachweis(File file) {
		try {
			PrintManagerLeistungsnachweis pml = new PrintManagerLeistungsnachweis(file);
			pml.generateXMLDocument();
		} catch (Exception el) {
			System.out.println("Could not Print Leistungsnachweis, because of: " + el.getMessage());
		}
	}

	private void generatePDFCertificate(File file) {
		try {
			PrintManagerCertificate pmc = new PrintManagerCertificate(file);
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
		colorizeRowsWithGoodGrades();
	}

	@Override
	public void init() {
		printButton.disableProperty().bind(Bindings.isEmpty(model.getStudentsWithCompleteRatingsProperty()));
		module = model.getContextModule();
		Platform.runLater(() -> {
			String text = String.format("%s %s", I18n.getString("view.module"), module.getName());
			casTitleLabel.setText(text);
		});
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

	/**
	 *  colors a row in green, if the corresponding student has good enough grades (>=50%)
	 *  */
	void colorizeRowsWithGoodGrades() {
        table.setRowFactory(new Callback<TableView<Data>, TableRow<Data>>() {
            @Override
            public TableRow<Data> call(TableView<Data> tableView) {
                final TableRow<Data> row = new TableRow<Data>() {
                    @Override
                    protected void updateItem(Data data, boolean empty){
                        super.updateItem(data, empty);
                        if(data == null) return;
                        if (model.getStudentsWithGoodGradesProperty().contains(data.getStudent())) {
                            setStyle("-fx-background-color: lightgreen;");
                        } else {
                            setStyle("");
                        }
                    }
                };
                return row;
            }
        });
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

		public Person getStudent() {
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
