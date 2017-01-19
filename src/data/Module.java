package data;

import java.sql.Date;

public class Module {

	private String name;
	private String shortName;
	private Date startDate;
	private Date endDate;
	
	
	public Module(
			String name,
			String shortName,
			Date startDate,
			Date endDate)
	{
		this.name = name;
		this.shortName = shortName;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	public void print() {
		System.out.println("Module: " + name + " (" + shortName + ")");
	}
}
