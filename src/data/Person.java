package data;

public class Person {
	private int id;
	private String lastName;
	private String firstName;
	private String sex;
	private String userName;
	private String password;
	
	
	public Person(
			int id,
			String firstName,
			String lastName,
			String sex,
			String userName,
			String password
			) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.sex = sex;
		this.userName = userName;
		this.password = password;
	}
	
	public void print() {
		System.out.format("Person %d: %s %s (%s)\r\n", getId(), getFirstName(), getLastName(), getUserName());
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return firstName + " " + lastName;
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
