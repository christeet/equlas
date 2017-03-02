package equals;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import controller.EqualsController;
import data.Course;
import data.Module;
import data.Person;
import data.Student;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import persistence.CourseDAO;
import persistence.DAOFactory;
import persistence.ModuleDAO;
import persistence.PersonDAO;
import persistence.RatingDAO;
import resources.I18n;
import util.Prefs;
import view.EqualsView;
import view.ViewLoader;

public class Main extends Application {

	private ScheduledExecutorService preferencesSaveExecutor = Executors.newScheduledThreadPool(1);
	private ScheduledFuture<?> preferencesSaveTask = null;
	
	@Override
	public void start(Stage stage) {
		
		// get locale as set in properties file (or system default)
		I18n.setLocale(Locale.forLanguageTag(Prefs.get().getLocale()));
		
		EqualsModel model = new EqualsModel();
		EqualsController controller = new EqualsController(model);

		EqualsView mainWindow = ViewLoader.create(getClass().getResource(
				"../view/MainContainerView.fxml")
				, model, controller);
		
		Scene scene = new Scene(mainWindow.getRootNode(), Prefs.get().getWindowWidth(), Prefs.get().getWindowHeight());
		
		scene.widthProperty().addListener((obs, old, newSceneWidth) -> {
			if(!stage.isMaximized()) {
				Prefs.get().setWindowWidth((double)newSceneWidth);
				savePreferencesDelayed();
			}
		});
		
		scene.heightProperty().addListener((obs, old, newSceneHeight) -> {
			if(!stage.isMaximized()) {
				Prefs.get().setWindowHeight((double)newSceneHeight);
				savePreferencesDelayed();
			}
		});

		stage.setMaximized(Prefs.get().getMaximized());
		stage.maximizedProperty().addListener((obs, old, maximized) -> {
			Prefs.get().setMaximized(maximized);
			Prefs.save();
		});
		stage.setTitle(I18n.getString("login.title"));
		stage.setScene(scene);
		stage.show();
	}
	/**
	 * The entry-point of this program.
	 * @param args commandline arguments
	 */
	public static void main(String[] args) {
		launch(args);
		//consoleDebug();
	}

	private void savePreferencesDelayed() {
		if(null != preferencesSaveTask) preferencesSaveTask.cancel(false);
		preferencesSaveTask = preferencesSaveExecutor.schedule(() -> {
			Prefs.save();
		}, 1, TimeUnit.SECONDS);
	}
	
	private static void consoleDebug() {
		System.out.println("testing Database:");
		
		DAOFactory dao = DAOFactory.getInstance();
		ModuleDAO moduleDAO = dao.createModuleDAO();
		ArrayList<Module> modules = null;
		try {
			modules = moduleDAO.getAllModules();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		CourseDAO courseDAO = dao.createCourseDAO();
		PersonDAO personDAO = dao.createPersonDAO();
		RatingDAO ratingDAO = dao.createRatingDAO();
		for(Module m : modules) {
			System.out.println("\r\n------------------------------------------------------------------------------");
			m.print();

			try {
				System.out.println("-- Courses --");
				ArrayList<Course> courses = courseDAO.getCoursesByModule(m);
				for(Course c : courses) {
					c.print();
				}
				System.out.println("-- Students --");
				ArrayList<Student> students = personDAO.getStudentsByModule(m);
				for(Student s : students) {
					s.print();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while(true) {
			try {

				System.out.println("R - set Rating");
				System.out.println("r - get Rating");
				System.out.println("u - get User");
				System.out.println("q - quit");
				String command = br.readLine();
				switch (command.charAt(0)){
				case 'R':
					setRating(br, ratingDAO);
					break;
				case 'u':
					getUser(br, personDAO);
					break;
				case 'q':
					return;
				default:
					System.out.println("no valid input");
					break;
				}
				
				
			}
			catch (Exception e) {
				System.err.println("try again.");
			}
		}
	}

	private static void getUser(BufferedReader br, PersonDAO personDAO) throws Exception {
		System.out.println("Username?");
		String username = br.readLine();
		try {
			Person person = personDAO.getPersonByUserName(username);
			System.out.format("Hello, %s %s\r\n", 
					person.getFirstName(), 
					person.getLastName());		
		} catch (SQLException e) {
			System.out.println("Could not find this user!");	
		}
	}
	
	private static void setRating(BufferedReader br, RatingDAO ratingDAO) throws Exception {
		System.out.println("UserId?");
		int userId = Integer.parseInt(br.readLine());
		System.out.println("CourseId?");
		int courseId = Integer.parseInt(br.readLine());
		System.out.println("Rating?");
		int rating = Integer.parseInt(br.readLine());
		try {
			ratingDAO.setRating(userId, courseId, rating);
			System.out.format("User %d has now Rating %d for Course %d\r\n", 
					userId, rating, courseId);	
		} catch (SQLException e) {
			System.out.println(e.getMessage());	
		}
	}

}
