package model;

import java.sql.SQLException;

import dao.DAOFactory;
import dao.PersonDAO;
import data.Person;
import util.IObservable;

public class UserLogin extends IObservable<UserLogin> {
	
	public enum LoginState { LOGGED_OUT, LOGGED_IN, LOGIN_FAILED };
	private LoginState loginState;
	private String username;
	
	public UserLogin() {
		this.loginState = LoginState.LOGGED_OUT;
	}
	
	public void checkPassword(String password, String username) {
		this.username = username;
		loginState = LoginState.LOGIN_FAILED;
		try {
			if(getUser().getPassword().equals(String.valueOf(password.hashCode()))) {
				loginState = LoginState.LOGGED_IN;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			this.setChanged();
			this.notifyObservers();
		}
	}
	
	public String getUsername() {
		return  username;
	}
	
	private Person getUser() {
		return getDAOUser();
	}
	
	private Person getDAOUser() {
		PersonDAO personDAO = DAOFactory.getInstance().createPersonDAO();
		Person person = null;
		try {
			person = personDAO.getPersonByUserName(username);
		} catch (SQLException e) {
			System.out.println("Could not get Person from DAOFactory!");
		}
		return person;
	}
	
	public LoginState getLoginState() { return loginState; }
	
}
