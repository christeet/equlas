package test;

import static org.junit.Assert.assertEquals;

import java.sql.Date;

import org.junit.Before;
import org.junit.Test;

import data.Module;
import data.UserRole;

public class ModuleTest {
	
	private Module module1;
	private Module module2;
	private Date startTime = new Date(2016-03-23);
	private Date endTime = new Date(2016-10-25);
	private UserRole userRole1 = UserRole.STUDENT;
	private UserRole userRole2 = UserRole.HEAD;
	
	@Before
	public void init() {
		module1 = new Module(1, "Module1", "mod1", startTime, endTime, userRole1);
		module2 = new Module(2, "Module2", "mod2", startTime, endTime, userRole2);
	}

	@Test
	public void testModule1Id() {
		assert(module1.getId() == 1);
	}
	
	@Test
	public void testModule2Id() {
		assert(module2.getId() == 2);
	}
	
	@Test
	public void testGetModule1Name() {
		assert(module1.getName() == "Module1");
	}
	
	@Test
	public void testGetModule2Name() {
		assert(module2.getName() == "Module2");
	}
	
	@Test
	public void testGetModule1ShortName() {
		assert(module1.getShortName() == "mod1");
	}
	
	@Test
	public void testGetModule2ShortName() {
		assert(module2.getShortName() == "mod2");
	}
	
	@Test
	public void testGetModule1StartDate() {
		assertEquals(module1.getStartDate(), startTime);
	}
	
	@Test
	public void testGetModule2StartDate() {
		assertEquals(module2.getStartDate(), startTime);
	}
	
	@Test
	public void testGetModule1EndDate() {
		assertEquals(module1.getEndDate(), endTime);
	}
	
	@Test
	public void testGetModule2EndDate() {
		assertEquals(module2.getEndDate(), endTime);
	}
	
	@Test
	public void testGetModule1UserRole() {
		assertEquals(module1.getUserRole(), userRole1);
	}
	
	@Test
	public void testGetModule2UserRole() {
		assertEquals(module2.getUserRole(), userRole2);
	}
	
	@Test
	public void testGetModule1ToString() {
		assertEquals(module1.toString(), module1.getName() + "\r\n" + module1.getShortName());
	}
	
	@Test
	public void testGetModule2ToString() {
		assertEquals(module2.toString(), module2.getName() + "\r\n" + module2.getShortName());
	}

}
