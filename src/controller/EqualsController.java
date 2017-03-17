package controller;

import data.Course;
import data.Module;
import equals.EqualsModel;

public class EqualsController {
	
	private EqualsModel model;
	
	/**
	 * Instantiates a new EqualsController for the given EqualsModel
	 * @param model The EqualsModel
	 */
	public EqualsController(EqualsModel model) {
		this.model = model;
	}
	
	/**
	 * Attempts a login with the given username and password
	 * @param password The unencrypted password
	 * @param username The username
	 */
	public void loginDataEntered(String password, String username) {
		this.model.getUserLogin().checkPassword(password, username);
	}
	
	/**
	 * Logs out the current user.
	 */
	public void logout() {
		this.model.getUserLogin().logout();
	}
	
	/**
	 * Triggers the EqualsModel to load Ratings and Students by the given Module
	 * @param module The selected Module
	 */
	public void selectedModuleChanged(Module module) {
		model.setSelectedModule(module);
	}


	/**
	 * Inserts or updates a new success-rate to the database
	 * @param studentId	The ID of the student
	 * @param course The course
	 * @param newSuccessRate The new success-rate
	 * @return true, if successrate could be updated/inserted.
	 */
	public boolean setNewSuccessRate(int studentId, Course course, int newSuccessRate) {
		return model.setNewSuccessRate(studentId, course, newSuccessRate);
	}

	/**
	 * Removes a success-rate from the database
	 * @param studentId	The ID of the student
	 * @param course The course
	 * @return true, if rating could be removed successfully
	 */
	public boolean removeRating(int studentId, Course course) {
		return model.removeRating(studentId, course);
	}
}
