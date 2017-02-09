package model;

import java.sql.SQLException;

import dao.DAOFactory;
import dao.PersonDAO;
import data.Person;
import util.IObservable;

public class UserLogin extends IObservable<UserLogin> {
	
	private String username;
	
	public UserLogin() {
	}
	
	public boolean checkPassword(String password, String username) {
		this.username = username;
		boolean loggedIn = false;
		try {
			loggedIn = getUser().getPassword().equals(String.valueOf(password.hashCode()));
		} catch (Exception e) {
			System.out.println(e.getMessage());
			loggedIn = false;
		}
		this.setChanged();
		this.notifyObservers();
		return loggedIn;
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
	
}
