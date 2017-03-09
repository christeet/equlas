package equals;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import data.Course;
import data.Module;
import data.Person;
import data.Rating;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.CourseDAO;
import persistence.DAOFactory;
import persistence.ModuleDAO;
import persistence.PersonDAO;
import persistence.RatingDAO;
import util.IObserver;

public class EqualsModel implements IObserver<UserLogin> {

	private BooleanProperty loggedIn = new SimpleBooleanProperty();
	private UserLogin userLogin;
	private Module contextModule = null;
	private RatingDAO ratingDao;

	private ObservableList<Module> moduleList = FXCollections.observableArrayList();
	private ObservableList<Course> coursesList = FXCollections.observableArrayList();
	private ObservableList<Person> studentList = FXCollections.observableArrayList();
	private ObservableList<Rating> ratingList = FXCollections.observableArrayList();
	
	public EqualsModel() {
		userLogin = new UserLogin();
		userLogin.addObserver(this);

		ratingDao = DAOFactory.getInstance().createRatingDAO();
	}
	
	public BooleanProperty loggedInProperty() {
		return loggedIn;
	}
	
	public UserLogin getUserLogin() { return userLogin; }

	@Override
	public void update(UserLogin o) {
		switch(userLogin.getLoginState()) {
		case LOGGED_IN:
			getModulesByUser();
			getCoursesByModules();
			break;
		case LOGGED_OUT:
			break;
		case LOGIN_FAILED:
			break;
		default:
			break;
		}
	}
	
	private void getModulesByUser() {
		ModuleDAO moduleDao = DAOFactory.getInstance().createModuleDAO();
		Person user = userLogin.getUser();
		try {
			ArrayList<Module> modules = new ArrayList<>();
			modules.addAll(moduleDao.getModulesByHead(user));
			modules.addAll(moduleDao.getModulesByTeacher(user));
			modules.addAll(moduleDao.getModulesByStudent(user));
			modules.addAll(moduleDao.getModulesByAssistant(user));
			moduleList.setAll(modules.stream().distinct().collect(Collectors.toList()));
			for(Module m : moduleList) {
				System.out.format("Module %d loaded: %s\r\n", m.getId(), m.getName());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void getCoursesByModules() {
		CourseDAO courseDao = DAOFactory.getInstance().createCourseDAO();
		Person user = userLogin.getUser();
		try {
			ArrayList<Course> courses = new ArrayList<>();
			for(Module m : moduleList) {
				courses.addAll(courseDao.getCoursesByModule(m));
			}
			coursesList.setAll(courses.stream().distinct().collect(Collectors.toList()));
			for(Course c : coursesList) {
				System.out.format("Course %d with ProfessorId %d of Module %d loaded: %s\r\n", 
						c.getId(), 
						c.getTeacherId(), 
						c.getModuleId(), 
						c.getName());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setSelectedModule(Module module) {
		if(module == contextModule || module == null) {
			return; // module already selected (or null); do nothing.
		}
		Person user = userLogin.getUser();
		switch(module.getUserRole()){
		case ASSISTANT:
			// get Ratings of all Students for all Courses of this Module
			getStudentsAndRatingsForModule(module);
			break;
		case HEAD:
			// get Ratings of all Students for all Courses of this Module
			getStudentsAndRatingsForModule(module);
			break;
		case STUDENT:
			// get Ratings for all Courses of this Module
			try {
				ratingList.addAll(ratingDao.getRatingListForStudent(user.getId()));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case TEACHER:
			// get Ratings of all Students for this teachers Courses of this Module
			getStudentsAndRatingsForModuleAndTeacher(module, user);
			break;
		default:
			return;
		}
		contextModule = module;
	}
	
	public void setSelectedCourse(Course course) {
		PersonDAO personDao = DAOFactory.getInstance().createPersonDAO();

		try {
			studentList.setAll(personDao.getStudentsByModule(course.getModule()));
			ratingList.addAll(ratingDao.getRatingListForCourse(course.getId()));

			/*for(Person s : studentList) {
				System.out.format("Student %d of Course %d (Module %d): %s\r\n", 
						s.getId(), 
						course.getId(),
						course.getModuleId(), 
						s.getName());
			}
			
			for(Rating r : ratingList) {
				System.out.format("Rating Student %d of Course %d: %d\r\n", 
						r.getStudentId(), 
						r.getCourseId(),
						r.getSuccessRate());
			}*/
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getStudentsAndRatingsForModule(Module module) {
		PersonDAO personDao = DAOFactory.getInstance().createPersonDAO();

		try {
			studentList.setAll(personDao.getStudentsByModule(module));
			ratingList.clear();
			for(Course c: coursesList) {
				ratingList.addAll(ratingDao.getRatingListForCourse(c.getId()));
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getStudentsAndRatingsForModuleAndTeacher(Module module, Person teacher) {
		PersonDAO personDao = DAOFactory.getInstance().createPersonDAO();

		try {
			studentList.setAll(personDao.getStudentsByModule(module));
			ratingList.clear();
			for(Course c: coursesList.filtered(c -> c.getTeacherId() == teacher.getId())) {
				ratingList.addAll(ratingDao.getRatingListForCourse(c.getId()));
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setNewSuccessRate(int studentId, int courseId, int newSuccessRate) {
		try {
			System.out.println("setting new SuccessRate");
			Rating newRating = ratingDao.setRating(studentId, courseId, newSuccessRate);
			ratingList.removeIf(r -> r.getStudentId() == studentId
								  && r.getCourseId() == courseId);
			ratingList.add(newRating);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void removeRating(int studentId, int courseId) {
		try {
			System.out.println("remove rating");
			ratingDao.removeRating(studentId, courseId);
			ratingList.removeIf(r -> r.getStudentId() == studentId
								  && r.getCourseId() == courseId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Module getContextModule() {
		return contextModule;
	}
	
	public ObservableList<Module> getModuleListProperty() {
		return moduleList;
	}
	
	public ObservableList<Course> getCoursesListProperty() {
		return coursesList;
	}
	
	public ObservableList<Person> getStudentListProperty() {
		return studentList;
	}
	
	public ObservableList<Rating> getRatingListProperty() {
		return ratingList;
	}
}
