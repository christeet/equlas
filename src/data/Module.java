package data;

import java.sql.Date;

public class Module {

	private int id;
	private String name;
	private String shortName;
	private Date startDate;
	private Date endDate;
	private UserRole userRole;
	
	
	public Module(
			int id,
			String name,
			String shortName,
			Date startDate,
			Date endDate,
			UserRole userRole)
	{
		this.id = id;
		this.name = name;
		this.shortName = shortName;
		this.startDate = startDate;
		this.endDate = endDate;
		this.userRole = userRole;
	}
	
	public void print() {
		System.out.format("Module %d: %s (%s)\r\n", getId(), getName(), getShortName());
	}

	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getShortName() {
		return shortName;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public UserRole getUserRole() {
		return userRole;
	}
	
	@Override
	public String toString() {
		return String.format("%s\r\n%s", getName(), getShortName());
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this) return true;
		Module other = (Module) obj;
		if(other == null) return false;
		return other.hashCode() == this.hashCode();
	}

	@Override
	public int hashCode() {
		return this.name.hashCode() + this.id; // very bad hashCode, but it fits our purpose.
	}
	
}
