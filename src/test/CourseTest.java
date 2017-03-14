package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.sql.Date;

import org.junit.Before;
import org.junit.Test;

import data.Course;
import data.Module;
import data.UserRole;

public class CourseTest {
	
	private Module module1;
	private Module module2;
	private Date startTime = new Date(2016-03-23);
	private Date endTime = new Date(2016-10-25);
	private Course course1;
	private Course course2;
	
	@Before
	public void init() {
		module1 = new Module(1, "Module1", "mod1", startTime, endTime, UserRole.STUDENT);
		module2 = new Module(2, "Module2", "mod2", startTime, endTime, UserRole.HEAD);
		course1 = new Course(1, "Java", "jav", 1.8f, module1, 1);
		course2 = new Course(2, "XML", "xml", 1.6f, module2, 2);
	}

	@Test
	public void testCourse1Id() {
		assert(course1.getId() == 1);
	}
	
	@Test
	public void testCourse2Id() {
		assert(course2.getId() == 2);
	}
	
	@Test
	public void testGetCourse1Name() {
		assert(course1.getName() == "Java");
	}
	
	@Test
	public void testGetCourse2Name() {
		assert(course2.getName() == "XML");
	}
	
	@Test
	public void testGetCourse1ShortName() {
		assert(course1.getShortName() == "jav");
	}
	
	@Test
	public void testGetCourse2ShortName() {
		assert(course2.getShortName() == "xml");
	}
	
	@Test
	public void testGetCourse1Weight() {
		assert(course1.getWeight() == 1.8f);
	}
	
	@Test
	public void testGetCourse2Weight() {
		assert(course2.getWeight() == 1.6f);
	}
	
	@Test
	public void testGetCourse1ModuleId() {
		assertEquals(course1.getModuleId(), module1.getId());
	}
	
	@Test
	public void testGetCourse2ModuleId() {
		assertEquals(course2.getModuleId(), module2.getId());
	}
	
	@Test
	public void testGetCourse1Module() {
		assertEquals(course1.getModule(), module1);
	}
	
	@Test
	public void testGetCourse2Module() {
		assertEquals(course2.getModule(), module2);
	}
	
	@Test
	public void testGetFailCourse1Module() {
		assertFalse(course1.getModule() == module2);
	}
	
	@Test
	public void testGetFailCourse2Module() {
		assertFalse(course2.getModule() == module1);
	}

}
