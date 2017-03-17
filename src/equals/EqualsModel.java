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

/**
 * The class "EqualsModel" fetches and holds all data to represent the state of
 * the program. It provides observable Lists to modules, courses, students,
 * ratings and semester. Additionally it provides information about the
 * currently selected module ("contextModule") and the currently selected
 * course, as well as the currently logged in User.
 * 
 * @author sbol
 *
 */
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

	/**
	 * The EqualsModel constructor instantiates a UserLogin and registers itself
	 * as observer.
	 */
	public EqualsModel() {
		userLogin = new UserLogin();
		userLogin.addObserver(this);
	}

	/**
	 * Observable Boolean to get whether a User is logged in or not.
	 * 
	 * @return
	 */
	public BooleanProperty loggedInProperty() {
		return loggedIn;
	}

	/**
	 * Returns the UserLogin instance of this EqualsModel.
	 * 
	 * @return
	 */
	public UserLogin getUserLogin() {
		return userLogin;
	}

	/**
	 * Update-callback of the UserLogin. Gets called when a user has logged in
	 * or out.
	 */
	@Override
	public void update(UserLogin o) {
		switch (o.getLoginState()) {
		case LOGGED_IN:
			getInitialDataByUser(o.getUser());
			break;
		case LOGGED_OUT:
			clearData();
			break;
		case LOGIN_FAILED:
			break;
		default:
			break;
		}
	}

	/**
	 * Loads Students and Ratings related to the selected module, depending on
	 * the UserRole of the logged in User.
	 * 
	 * @param module
	 *            the module that got selected in a View
	 */
	public void setSelectedModule(Module module) {
		if (module == contextModule || module == null) {
			return; // module already selected (or null): do nothing.
		}
		contextModule = module;
		Person user = userLogin.getUser();
		switch (module.getUserRole()) {
		case ASSISTANT:
			// get Ratings of all Students for all Courses of this Module
			studentList.setAll(getStudentsByModule(module));
			ratingList.setAll(getRatingsFromCourses(courseList.filtered(c -> c.getModuleId() == module.getId())));
			break;
		case HEAD:
			// get Ratings of all Students for all Courses of this Module
			studentList.setAll(getStudentsByModule(module));
			ratingList.setAll(getRatingsFromCourses(courseList.filtered(c -> c.getModuleId() == module.getId())));
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
			// get Ratings of all Students for this teachers Courses of this
			// Module
			studentList.setAll(getStudentsByModule(module));
			ratingList.setAll(getRatingsFromCourses(courseList.filtered(
					c -> c.getModuleId() == module.getId() 
				      && c.getTeacherId() == user.getId())));
			break;
		default:
			return;
		}
	}

	/**
	 * Inserts or updates a new success-rate to the database
	 * @param studentId	The ID of the student
	 * @param course The course
	 * @param newSuccessRate The new success-rate
	 * @return true, if successrate could be updated/inserted.
	 */
	public boolean setNewSuccessRate(int studentId, Course course, int newSuccessRate) {
		try {
			System.out.format("setting new SuccessRate %d for student %d in course %s\r\n", newSuccessRate, studentId,
					course.getShortName());

			Optional<Rating> oldRating = ratingList.stream()
					.filter(r -> r.getStudentId() == studentId 
							  && r.getCourseId() == course.getId()).findFirst();
			Rating newRating = ratingDao.setRating(studentId, course, newSuccessRate,
					oldRating.isPresent() ? oldRating.get().getVersion() : -1);
			ratingList.removeIf(r -> r.getStudentId() == studentId && r.getCourseId() == course.getId());
			ratingList.add(newRating);
			return true;
		} catch (SQLException | NullPointerException e) {
			e.printStackTrace();
		} catch (OptimisticLockingException e) {
			System.err.format(
					"Failed to set rating %d for Student %d in Course %d " 
			      + "because of optimistic Locking!\r\n",
					newSuccessRate, studentId, course.getId());
			/*
			 * get all ratings for this course again and replace them in the
			 * ratingsList
			 */
			ArrayList<Rating> newCourseRatings = new ArrayList<>();
			try {
				newCourseRatings.addAll(ratingDao.getRatingListForCourse(course));
				ratingList.removeIf(r -> r.getStudentId() == studentId && r.getCourseId() == course.getId());
				ratingList.addAll(newCourseRatings.stream().filter(r -> r.getStudentId() == studentId)
						.collect(Collectors.toList()));

			} catch (SQLException | NullPointerException ex) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Removes a success-rate from the database
	 * @param studentId	The ID of the student
	 * @param course The course
	 * @return true, if rating could be removed successfully
	 */
	public boolean removeRating(int studentId, Course course) {
		try {
			System.out.println("remove rating");
			ratingDao.removeRating(studentId, course);
			ratingList.removeIf(r -> r.getStudentId() == studentId && r.getCourseId() == course.getId());
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Gets the Module, for which all current model-data is valid.
	 * @return the Module
	 */
	public Module getContextModule() {
		return contextModule;
	}

	/**
	 * Gets a list of all Modules where the User is/was Head/Teacher/Student/Assistant
	 * @return an observable list of Modules
	 */
	public ObservableList<Module> getModuleListProperty() {
		return moduleList;
	}

	/**
	 * Gets a list of all Courses where the User is/was Head/Teacher/Student/Assistant
	 * @return an observable list of Courses
	 */
	public ObservableList<Course> getCoursesListProperty() {
		return courseList;
	}

	/**
	 * Gets a list of all Students of the current contextModule
	 * @return an observable list of Persons (Students)
	 */
	public ObservableList<Person> getStudentListProperty() {
		return studentList;
	}

	/**
	 * Gets a list of all Ratings of all Courses of the current contextModule
	 * @return an observable list of Ratings
	 */
	public ObservableList<Rating> getRatingListProperty() {
		return ratingList;
	}

	/**
	 * Gets a list of all available semester-tags of the current User's Modules.
	 * The semester-tags may look like "HS15", "FS16" etc
	 * @return an observable list of semester-tags
	 */
	public ObservableList<String> getSemestersProperty() {
		return semesterList;
	}

	/**
	 * Gets a list of all Students who have a Rating for each Course in the current contextModule.
	 * @return an observable list of Persons (Students)
	 */
	public ObservableList<Person> getStudentsWithCompleteRatingsProperty() {
		return studentList.filtered(s -> {
			boolean ratingsMissing = courseList.stream().filter(c -> c.getModuleId() == contextModule.getId())
					.anyMatch(c -> ratingList.stream().filter(r -> r.getStudentId() == s.getId())
							.mapToInt(Rating::getCourseId)
							.noneMatch(ratingCourseId -> c.getId() == ratingCourseId));
			return !ratingsMissing;
		});
	}


	/**
	 * Gets a list of all Students who have a Rating for each Course in the current contextModule
	 * AND whose total grades are >= 50%
	 * @return an observable list of Persons (Students)
	 */
	public ObservableList<Person> getStudentsWithGoodGradesProperty() {
		return getStudentsWithCompleteRatingsProperty().filtered(s -> {
			// get ratings multiplied by their corresponding weights:
			double totalRatings = ratingList.stream()
					// the ratings of all students of this module:
					.filter(r -> r.getStudentId() == s.getId() && r.getModuleId() == contextModule.getId())
					// multiply successRates by the weight of the corresponding
					// course and sum up:
					.mapToDouble(r -> r.getSuccessRate() * courseList.stream()
							.filter(c -> c.getId() == r.getCourseId())
							.findFirst().get().getWeight())
					.sum();
			// sum all course-weights:
			double totalWeight = courseList.stream().mapToDouble(Course::getWeight).sum();

			/* System.out.format("student %s: ratings=%f, weights=%f, grade=%f\r\n",
			 	s.getName(), totalRatings, totalWeight, totalRatings / totalWeight);
			 */

			return totalRatings / totalWeight >= 50.0;
		});
	}

	private void getInitialDataByUser(Person user) {
		ratingDao = DAOFactory.getInstance().createRatingDAO();
		moduleList.setAll(getModulesByUser(user));
		semesterList.setAll(getSemestersByModules(moduleList));
		courseList.setAll(getCoursesByModules(moduleList));
	}

	private void clearData() {
		moduleList.clear();
		courseList.clear();
		studentList.clear();
		ratingList.clear();
		semesterList.clear();
	}

	private List<Module> getModulesByUser(Person user) {
		ModuleDAO moduleDao = DAOFactory.getInstance().createModuleDAO();
		ArrayList<Module> modules = new ArrayList<>();
		try {
			modules.addAll(moduleDao.getModulesByHead(user));
			modules.addAll(moduleDao.getModulesByTeacher(user));
			modules.addAll(moduleDao.getModulesByStudent(user));
			modules.addAll(moduleDao.getModulesByAssistant(user));
			for (Module m : moduleList) {
				System.out.format("Module %d loaded: %s\r\n", m.getId(), m.getName());
			}

		} catch (SQLException | NullPointerException e) {
			e.printStackTrace();
		}
		return modules.stream().distinct().collect(Collectors.toList());
	}

	private List<String> getSemestersByModules(List<Module> modules) {
		ArrayList<String> semesters = new ArrayList<String>();
		for (Module m : modules) {
			System.out.format("Module %d loaded: %s\r\n", m.getId(), m.getName());

			String semesterTag = m.getShortName().substring(m.getShortName().length() - 4);

			if (!semesters.contains(semesterTag)) {
				semesters.add(semesterTag);
			}
		}
		/* sort semesterList first by year, then by semester */
		semesters.sort((a, b) -> {
			int byYear = b.substring(2).compareTo(a.substring(2));
			if (byYear != 0)
				return byYear;
			return b.substring(0, 2).compareTo(a.substring(0, 2)); // by HS > FS
		});

		return semesters;
	}

	private List<Course> getCoursesByModules(List<Module> modules) {
		CourseDAO courseDao = DAOFactory.getInstance().createCourseDAO();
		ArrayList<Course> courses = new ArrayList<>();
		try {
			for (Module m : modules) {
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
			for (Course c : courses) {
				ratings.addAll(ratingDao.getRatingListForCourse(c));
			}
		} catch (SQLException | NullPointerException e) {
			e.printStackTrace();
		}
		return ratings;
	}
}
