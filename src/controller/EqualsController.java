package controller;

import data.Module;
import model.EqualsModel;

public class EqualsController {
	
	private EqualsModel model;
	
	public EqualsController(EqualsModel model) {
		this.model = model;
	}
	
	public void loginDataEntered(String password, String username) {
		this.model.getUserLogin().checkPassword(password, username);
	}
	
	public void selectedModuleChanged(Module module) {
		model.setSelectedModule(module);
	}
}
