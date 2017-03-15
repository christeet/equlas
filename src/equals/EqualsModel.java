package equals;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
import persistence.OptimisticLockingException;
import persistence.PersonDAO;
import persistence.RatingDAO;
import util.IObserver;

public class EqualsModel implements IObserver<UserLogin> {

	private BooleanProperty loggedIn = new SimpleBooleanProperty();
	private UserLogin userLogin;
	private Module contextModule = null;
	private RatingDAO ratingDao;

	private ObservableList<Module> moduleList = FXCollections.observableArrayList();
	private ObservableList<Course> courseList = FXCollections.observableArrayList();
	private ObservableList<Person> studentList = FXCollections.observableArrayList();
	private ObservableList<Rating> ratingList = FXCollections.observableArrayList();
	private ObservableList<String> semesterList = FXCollections.observableArrayList();
	
	public EqualsModel() {
		userLogin = new UserLogin();
		userLogin.addObserver(this);
	}
	
	public BooleanProperty loggedInProperty() {
		return loggedIn;
	}
	
	public UserLogin getUserLogin() { return userLogin; }

	@Override
	public void update(UserLogin o) {
		switch(userLogin.getLoginState()) {
		case LOGGED_IN:
			ratingDao = DAOFactory.getInstance().createRatingDAO();
			moduleList.setAll(getModulesByUser(o.getUser()));
			semesterList.setAll(getSemestersByModules(moduleList));
			courseList.setAll(getCoursesByModules(moduleList));
			break;
		case LOGGED_OUT:
			break;
		case LOGIN_FAILED:
			break;
		default:
			break;
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
			studentList.setAll(getStudentsByModule(module));
			ratingList.setAll(getRatingsFromCourses(courseList));
			break;
		case HEAD:
			// get Ratings of all Students for all Courses of this Module
			studentList.setAll(getStudentsByModule(module));
			ratingList.setAll(getRatingsFromCourses(courseList));
			break;
		case STUDENT:
			// get Ratings for all Courses of this Module
			try {
				ratingList.addAll(ratingDao.getRatingListForStudent(user.getId(), module));
			} catch (SQLException | NullPointerException e) {
				e.printStackTrace();
			}
			break;
		case TEACHER:
			// get Ratings of all Students for this teachers Courses of this Module
			studentList.setAll(getStudentsByModule(module));
			ratingList.setAll(getRatingsFromCourses(courseList.filtered(c -> c.getTeacherId() == user.getId())));
			break;
		default:
			return;
		}
		contextModule = module;
	}
	
	public void setSelectedCourse(Course course) {
		try {
			studentList.setAll(getStudentsByModule(course.getModule()));
			ratingList.addAll(ratingDao.getRatingListForCourse(course));
		} catch (SQLException | NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	public boolean setNewSuccessRate(int studentId, Course course, int newSuccessRate) {
		try {
			System.out.format("setting new SuccessRate %d for student %d in course %s\r\n", 
					newSuccessRate, studentId, course.getShortName());
			
			Optional<Rating> oldRating = ratingList.stream().filter(r -> r.getStudentId() == studentId
								  && r.getCourseId() == course.getId()).findFirst();
			Rating newRating = ratingDao.setRating(
					studentId, 
					course, 
					newSuccessRate, 
					oldRating.isPresent() ? oldRating.get().getVersion() : 0);
			ratingList.removeIf(r -> r.getStudentId() == studentId
								  && r.getCourseId() == course.getId());
			ratingList.add(newRating);
			return true;
		} catch (SQLException | NullPointerException e) {
			e.printStackTrace();
		} catch (OptimisticLockingException e) {
			System.err.format("Failed to set rating %d for Student %d in Course %d "
							+ "because of optimistic Locking!\r\n", 
					newSuccessRate, studentId, course.getId());
			/* get all ratings for this course again and replace them in the ratingsList */
			ArrayList<Rating> newCourseRatings = new ArrayList<>();
			try {
				newCourseRatings.addAll(ratingDao.getRatingListForCourse(course));
				ratingList.removeIf(r -> r.getStudentId() == studentId
						  			  && r.getCourseId() == course.getId());
				ratingList.addAll(newCourseRatings.stream()
						.filter(r -> r.getStudentId() == studentId).collect(Collectors.toList()));
				
			} catch (SQLException | NullPointerException ex) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public boolean removeRating(int studentId, Course course) {
		try {
			System.out.println("remove rating");
			ratingDao.removeRating(studentId, course);
			ratingList.removeIf(r -> r.getStudentId() == studentId
								  && r.getCourseId() == course.getId());
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public Module getContextModule() {
		return contextModule;
	}
	
	public ObservableList<Module> getModuleListProperty() {
		return moduleList;
	}
	
	public ObservableList<Course> getCoursesListProperty() {
		return courseList;
	}
	
	public ObservableList<Person> getStudentListProperty() {
		return studentList;
	}
	
	public ObservableList<Rating> getRatingListProperty() {
		return ratingList;
	}
	
	public ObservableList<String> getSemestersProperty() {
		return semesterList;
	}
	
	public ObservableList<Person> getStudentsWithCompleteRatingsProperty() {
		return studentList.filtered(s -> {
			boolean ratingsMissing = courseList.stream().filter(c -> c.getModuleId() == contextModule.getId())
					.anyMatch(c -> ratingList.stream().filter(r -> r.getStudentId() == s.getId())
					.mapToInt(Rating::getCourseId)
					.noneMatch(ratingCourseId -> c.getId()==ratingCourseId));
			return !ratingsMissing;
		});
	}
	
	public ObservableList<Person> getStudentsWithGoodGradesProperty() {
		return getStudentsWithCompleteRatingsProperty().filtered(s -> {
			/* get ratings multiplied by their corresponding weights */
			double totalRatings = ratingList.stream()
					.filter(r -> r.getStudentId() == s.getId()
							  && r.getModuleId() == contextModule.getId())
					.mapToDouble(r -> r.getSuccessRate() * courseList.stream()
											.filter(c -> c.getId() == r.getCourseId())
											.findFirst().get().getWeight()
								).sum();
			/* sum all course-weights */
			double totalWeight = courseList.stream().filter(c -> c.getModuleId() == contextModule.getId())
					.mapToDouble(Course::getWeight).sum();

			/*System.out.format("student %s: ratings=%f, weights=%f, grade=%f\r\n", 
					s.getName(), totalRatings, totalWeight, totalRatings / totalWeight);*/
			
			return totalRatings / totalWeight >= 50.0;
		});
	}
	
	private List<Module> getModulesByUser(Person user) {
		ModuleDAO moduleDao = DAOFactory.getInstance().createModuleDAO();
		ArrayList<Module> modules = new ArrayList<>();
		try {
			modules.addAll(moduleDao.getModulesByHead(user));
			modules.addAll(moduleDao.getModulesByTeacher(user));
			modules.addAll(moduleDao.getModulesByStudent(user));
			modules.addAll(moduleDao.getModulesByAssistant(user));
			for(Module m : moduleList) {
				System.out.format("Module %d loaded: %s\r\n", m.getId(), m.getName());
			}
			
		} catch (SQLException | NullPointerException e) {
			e.printStackTrace();
		}
		return modules.stream().distinct().collect(Collectors.toList());
	}
	
	private List<String> getSemestersByModules(List<Module> modules) {
		ArrayList<String> semesters = new ArrayList<String>();
		for(Module m : modules) {
			System.out.format("Module %d loaded: %s\r\n", m.getId(), m.getName());
			
			String semesterTag = m.getShortName().substring(m.getShortName().length()-4);
			
			if(!semesters.contains(semesterTag)) {
				semesters.add(semesterTag);
			}
		}
		/* sort semesterList first by year, then by semester */
		semesters.sort((a, b) -> { 
			int byYear = b.substring(2).compareTo(a.substring(2));
			if(byYear != 0) return byYear;
			return b.substring(0,2).compareTo(a.substring(0,2)); // by HS > FS
		});
		
		return semesters;
	}
	
	private List<Course> getCoursesByModules(List<Module> modules) {
		CourseDAO courseDao = DAOFactory.getInstance().createCourseDAO();
		ArrayList<Course> courses = new ArrayList<>();
		try {
			for(Module m : modules) {
				courses.addAll(courseDao.getCoursesByModule(m));
			}
		} catch (SQLException | NullPointerException e) {
			e.printStackTrace();
		}
		return courses.stream().distinct().collect(Collectors.toList());
	}
	
	private List<Person> getStudentsByModule(Module module) {
		PersonDAO personDao = DAOFactory.getInstance().createPersonDAO();
		ArrayList<Person> students = new ArrayList<>();
		try {
			students.addAll(personDao.getStudentsByModule(module));
		} catch (SQLException | NullPointerException e) {
			e.printStackTrace();
		}
		return students;
	}
	
	private List<Rating> getRatingsFromCourses(List<Course> courses) {
		ArrayList<Rating> ratings = new ArrayList<>();
		try {
			for(Course c: courses) {
				ratings.addAll(ratingDao.getRatingListForCourse(c));
			}
		} catch (SQLException | NullPointerException e) {
			e.printStackTrace();
		}
		return ratings;
	}
}
