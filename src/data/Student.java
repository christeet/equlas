package data;

import java.sql.Date;

public class Student extends Person {
	
	private Date dateOfBirth;
	private String placeOfOrigin;
	
	
	public Student(
			String lastName,
			String firstName,
			String sex,
			String userName,
			String password,
			Date dateOfBirth,
			String placeOfOrigin
			)
	{
		super(lastName, firstName, sex, userName, password);
		this.dateOfBirth = dateOfBirth;
		this.placeOfOrigin = placeOfOrigin;
	}
	
	@Override
	public void print() {
		System.out.println("Student: " + getFirstName() + " " + getLastName() + "");
	}
	
	public Date getDateOfBirth() {
		return dateOfBirth;
	}
	
	public String getPlaceOfOrigin() {
		return placeOfOrigin;
	}
}
