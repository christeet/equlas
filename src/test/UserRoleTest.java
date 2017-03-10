package test;

import org.junit.Test;

import data.UserRole;

public class UserRoleTest {
	
	@Test
	public void testAssistant() {
		equals(UserRole.valueOf("ASSISTANT"));
	}
	
	@Test
	public void testHead() {
		equals(UserRole.valueOf("HEAD"));
	}
	
	@Test
	public void testTeacher() {
		equals(UserRole.valueOf("TEACHER"));
	}
	
	@Test
	public void testStudent() {
		equals(UserRole.valueOf("STUDENT"));
	}

}
