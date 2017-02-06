package model;

import java.io.IOException;
import java.sql.SQLException;

import controller.UserLoginController;
import dao.DAOFactory;
import dao.PersonDAO;
import data.Person;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import resources.I18n;

public class UserLogin extends Stage {
	
	private String username;
	private FXMLLoader loader;
	private Parent root;
	
	public UserLogin() {
		final String fxmlUrl = "../view/UserLoginView.fxml";
		
		try {
			loader = new FXMLLoader(getClass().getResource(fxmlUrl), I18n.getResourceBundle());
		  root = (Parent) loader.load();
		} catch (IOException e) {
			System.out.println("Could not load: " + e.getMessage());
		}
		loader.<UserLoginController>getController().init(this);
		
		Scene scene = new Scene(root, 200, 200);
		
		this.setTitle(I18n.getString("login.title"));
		this.setScene(scene);
	}
	
	public boolean checkPassword(String password, String username) {
		this.username = username;
		try {
		  return getUser().getPassword().equals(String.valueOf(password.hashCode()));
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
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
