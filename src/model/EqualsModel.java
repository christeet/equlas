package model;

import java.sql.SQLException;

import dao.CourseDAO;
import dao.DAOFactory;
import dao.ModuleDAO;
import data.Course;
import data.Module;
import data.Person;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import util.IObserver;

public class EqualsModel implements IObserver<UserLogin> {

	private BooleanProperty loggedIn = new SimpleBooleanProperty();
	private UserLogin userLogin;

	private ObservableList<Module> moduleList = FXCollections.observableArrayList();
	private ObservableList<Course> coursesList = FXCollections.observableArrayList();
	
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
			coursesList.clear();
			for(Module m : moduleList) {
				coursesList.addAll(courseDao.getCoursesByModuleAndTeacher(m, user));
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
		switch(module.getUserRole()){
		case ASSISTANT:
			// TODO: get Ratings of all Students for all Courses of this Module
			break;
		case HEAD:
			// TODO: get Ratings of all Students for all Courses of this Module
			break;
		case STUDENT:
			// TODO: get Ratings for all Courses of this Module
			break;
		case TEACHER:
			// do nothing
			break;
		default:
			break;
		}
	}
	
	public ObservableList<Module> getModuleListProperty() {
		return moduleList;
	}
	
	public ObservableList<Course> getCoursesListProperty() {
		return coursesList;
	}
}
