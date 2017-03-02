package model;

import java.sql.SQLException;

import dao.CourseDAO;
import dao.DAOFactory;
import dao.ModuleDAO;
import dao.RatingDAO;
import data.Course;
import data.Module;
import data.Person;
import data.Rating;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import util.IObserver;

public class EqualsModel implements IObserver<UserLogin> {

	private BooleanProperty loggedIn = new SimpleBooleanProperty();
	private UserLogin userLogin;
	private Module contextModule = null;
	RatingDAO ratingDao;

	private ObservableList<Module> moduleList = FXCollections.observableArrayList();
	private ObservableList<Course> coursesList = FXCollections.observableArrayList();
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
			getCoursesByUserAndModules();
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
			moduleList.clear();
			moduleList.addAll(moduleDao.getModulesByHead(user));
			moduleList.addAll(moduleDao.getModulesByTeacher(user));
			moduleList.addAll(moduleDao.getModulesByStudent(user));
			moduleList.addAll(moduleDao.getModulesByAssistant(user));
			for(Module m : moduleList) {
				System.out.format("Module %d loaded: %s\r\n", m.getId(), m.getName());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void getCoursesByUserAndModules() {
		CourseDAO courseDao = DAOFactory.getInstance().createCourseDAO();
		Person user = userLogin.getUser();
		try {
			//coursesList.clear();
			for(Module m : moduleList) {
				coursesList.addAll(courseDao.getCoursesByModuleAndTeacher(m, user));
				coursesList.addAll(courseDao.getCoursesByModuleAndStudent(m, user));
			}
			for(Course c : coursesList) {
				System.out.format("Course %d of Module %d loaded: %s\r\n", 
						c.getId(), 
						c.getModuleId(), 
						c.getName());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setSelectedModule(Module module) {
		Person user = userLogin.getUser();
		switch(module.getUserRole()){
		case ASSISTANT:
			// TODO: get Ratings of all Students for all Courses of this Module
			break;
		case HEAD:
			// TODO: get Ratings of all Students for all Courses of this Module
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
			// do nothing
			break;
		default:
			return;
		}
		contextModule = module;
	}
	
	public void setNewSuccessRate(int studentId, int courseId, int newSuccessRate) {
		try {
			System.out.println("setting new SuccessRate");
			ratingDao.setRating(studentId, courseId, newSuccessRate);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void removeRating(int studentId, int courseId) {
		try {
			System.out.println("remove rating");
			ratingDao.removeRating(studentId, courseId);
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
	
	public ObservableList<Rating> getRatingListProperty() {
		return ratingList;
	}
}
