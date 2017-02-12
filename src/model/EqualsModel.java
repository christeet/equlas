package model;

import java.sql.SQLException;

import dao.DAOFactory;
import dao.ModuleDAO;
import data.Module;
import data.Person;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import util.IObserver;

public class EqualsModel implements IObserver<UserLogin>{

	private BooleanProperty loggedIn = new SimpleBooleanProperty();
	private UserLogin userLogin;
	
	private ObservableList<Module> moduleList = FXCollections.observableArrayList();
	
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
			moduleList.setAll(moduleDao.getModulesByStudent(user));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ObservableList<Module> getModuleListProperty() {
		return moduleList;
	}
}
