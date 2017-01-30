package data;

import java.sql.Date;

public class Student extends Person {
	
	private Date dateOfBirth;
	private String placeOfOrigin;
	
	
	public Student(
			int id,
			String firstName,
			String lastName,
			String sex,
			String userName,
			String password,
			Date dateOfBirth,
			String placeOfOrigin
			)
	{
		super(id, firstName, lastName, sex, userName, password);
		this.dateOfBirth = dateOfBirth;
		this.placeOfOrigin = placeOfOrigin;
	}
	
	@Override
	public void print() {
		System.out.format("Student %d: %s %s (%s)\r\n", getId(), getFirstName(), getLastName(), getUserName());
	}
	
	public Date getDateOfBirth() {
		return dateOfBirth;
	}
	
	public String getPlaceOfOrigin() {
		return placeOfOrigin;
	}
}
