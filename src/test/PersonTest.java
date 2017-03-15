package test;

import static org.junit.Assert.assertEquals;

import java.sql.Date;

import org.junit.Before;
import org.junit.Test;

import data.Person;

public class PersonTest {
	
	private Person student1;
	private Person student2;
	private Date date1 = new Date(1985-5-26);
	private Date date2 = new Date(1978-9-05);

	@Before
	public void init() {
		student1 = new Person(1, "First1", "Person1", "m", "per1", "stud", date1, "Bern");
		student2 = new Person(2, "First2", "Person2", "w", "per2", "stud", date2, "Locarno");
	}

	@Test
	public void testGetStudent1Id() {
		assertEquals(student1.getId(), 1);
	}
	
	@Test
	public void testGetStudent2Id() {
		assertEquals(student2.getId(), 2);
	}
	
	@Test
	public void testGetStudent1FirstName() {
		assertEquals(student1.getFirstName(), "First1");
	}
	
	@Test
	public void testGetStudent2FirstName() {
		assertEquals(student2.getFirstName(), "First2");
	}
	
	@Test
	public void testGetStudent1LastName() {
		assertEquals(student1.getLastName(), "Person1");
	}
	
	@Test
	public void testGetStudent2LastName() {
		assertEquals(student2.getLastName(), "Person2");
	}
	
	@Test
	public void testGetStudent1Sex() {
		assertEquals(student1.getSex(), "m");
	}
	
	@Test
	public void testGetStudent2Sex() {
		assertEquals(student2.getSex(), "w");
	}
	
	@Test
	public void testGetStudent1UserName() {
		assertEquals(student1.getUserName(), "per1");
	}
	
	@Test
	public void testGetStudent2UserName() {
		assertEquals(student2.getUserName(), "per2");
	}
	
	@Test
	public void testGetStudent1Password() {
		assertEquals(student1.getPassword(), "stud");
	}
	
	@Test
	public void testGetStudent2Password() {
		assertEquals(student2.getPassword(), "stud");
	}
	
	@Test
	public void testGetStudent1DateOfBirth() {
		assertEquals(student1.getDateOfBirth(), date1);
	}
	
	@Test
	public void testGetStudent2DateOfBirth() {
		assertEquals(student2.getDateOfBirth(), date2);
	}
	
	@Test
	public void testGetStudent1PlaceOfOrigin() {
		assertEquals(student1.getPlaceOfOrigin(), "Bern");
	}
	
	@Test
	public void testGetStudent2PlaceOfOrigin() {
		assertEquals(student2.getPlaceOfOrigin(), "Locarno");
	}

}
