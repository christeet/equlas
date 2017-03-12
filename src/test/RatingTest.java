package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.sql.Date;

import org.junit.Before;
import org.junit.Test;

import data.Course;
import data.Module;
import data.Person;
import data.Rating;
import data.UserRole;

public class RatingTest {
	
	private Rating rating1;
	private Rating rating2;
	private Module module1;
	private Module module2;
	private Course course1;
	private Course course2;
	private Date startTime = new Date(2016-03-23);
	private Date endTime = new Date(2016-10-25);
	private Person student1;
	private Person student2;

	@Before
	public void init() {
		student1 = new Person(1, "Test1", "Person1", "m", "per1", "stud", new Date(1985-5-26), "Bern");
		student2 = new Person(2, "Test2", "Person2", "w", "per2", "stud", new Date(1978-9-05), "Locarno");
		module1 = new Module(1, "Module1", "mod1", startTime, endTime, UserRole.STUDENT);
		module2 = new Module(2, "Module2", "mod2", startTime, endTime, UserRole.HEAD);
		course1 = new Course(1, "Java", "jav", 1.8f, module1, 1);
		course2 = new Course(2, "XML", "xml", 1.6f, module2, 2);
		rating1 = new Rating(1, 1, 80, 1, 1);
		rating2 = new Rating(2, 2, 60, 2, 2);
		
	}

	@Test
	public void testGetStudent1Id() {
		assertEquals(rating1.getStudentId(), student1.getId());
	}
	
	@Test
	public void testGetStudent2Id() {
		assertEquals(rating2.getStudentId(), student2.getId());
	}
	
	@Test
	public void testGetFailStudent2Id() {
		assertFalse(rating2.getStudentId() == student1.getId());
	}
	
	@Test
	public void testGetCourse1Id() {
		assertEquals(rating1.getCourseId(), course1.getId());
	}
	
	@Test
	public void testGetCourse2Id() {
		assertEquals(rating2.getCourseId(), course2.getId());
	}
	
	@Test
	public void testGetFailCourse1Id() {
		assertFalse(rating1.getCourseId() == course2.getId());
	}
	
	@Test
	public void testGetSuccessrate1() {
		assert(rating1.getSuccessRate() == 80);
	}
	
	@Test
	public void testGetSuccessrate2() {
		assert(rating2.getSuccessRate() == 60);
	}
	
	@Test
	public void testGetFailSuccessrate2() {
		assertFalse(rating2.getSuccessRate() == 80);
	}
	
	@Test
	public void testGetVersion1() {
		assert(rating1.getVersion() == 1);
	}
	
	@Test
	public void testGetVersion2() {
		assert(rating2.getVersion() == 2);
	}
	
	@Test
	public void testGetFailVersion1() {
		assertFalse(rating1.getVersion() == 2);
	}
	
}
