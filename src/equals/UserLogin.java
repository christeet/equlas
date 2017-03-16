package equals;

import java.sql.SQLException;

import data.Person;
import persistence.DAOFactory;
import persistence.PersonDAO;
import util.IObservable;
import util.Prefs;

public class UserLogin extends IObservable<UserLogin> {
	
	public enum LoginState { LOGGED_OUT, LOGGED_IN, LOGIN_FAILED };
	private LoginState loginState;
	private String username;
	private Person currentUser;
	
	/**
	 * Instantiates a 
	 */
	public UserLogin() {
		this.loginState = LoginState.LOGGED_OUT;
	}

	
	/**
	 * Attempts a login with the given username and password 
	 * and notifies all Observers about the new LoginState
	 * @param password The unencrypted password
	 * @param username The username
	 */
	public void checkPassword(String password, String username) {
		
		//***************** <DEBUG CODE FOR CONVENIENCE>  ****************************************/
		//if(username.isEmpty()) { username="swp1";}
		//if(password.isEmpty()) { password="stud"; }
		//***************** </DEBUG CODE FOR CONVENIENCE> ****************************************/
		
		this.username = username;
		loginState = LoginState.LOGIN_FAILED;
		try {
			Person p = getUserFromDatabase();
			if(p.getPassword().equals(String.valueOf(password.hashCode()))) {
				loginState = LoginState.LOGGED_IN;
				currentUser = p;
				Prefs.get().setLastLoggedInUser(username);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			this.setChanged();
			this.notifyObservers();
		}
	}

	/**
	 * Logs out the current user.
	 */
	public void logout() {
		loginState = LoginState.LOGGED_OUT;
		this.setChanged();
		this.notifyObservers();
	}
	
	public String getUsername() {
		return  username;
	}
	
	public Person getUser() {
		return currentUser;
	}
	
	private Person getUserFromDatabase() {
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
