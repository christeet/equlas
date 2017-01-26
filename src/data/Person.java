package data;

import java.sql.Date;

public class Person {

	private String lastName;
	private String firstName;
	private String sex;
	private String userName;
	private String password;
	
	
	public Person(
			String lastName,
			String firstName,
			String sex,
			String userName,
			String password
			)
	{
		this.lastName = lastName;
		this.firstName = firstName;
		this.sex = sex;
		this.userName = userName;
		this.password = password;
	}
	
	public void print() {
		System.out.println("Person: " + firstName + " " + lastName + "");
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public String getSex() {
		return sex;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public String getPassword() {
		return password;
	}
}
