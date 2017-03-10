package test;

import org.junit.Before;
import org.junit.Test;

import equals.EqualsModel;

public class EqualsModelTest {
	
	private EqualsModel model;

	@Before
	public void init() {
		model = new EqualsModel();
	}

	@Test
	public void testUserLogin() {
		equals(model.getUserLogin() != null);
	}
	
	@Test
	public void testgetContextModule() {
		equals(model.getContextModule() != null);
	}
	
}
