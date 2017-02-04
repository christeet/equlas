package data;

import java.sql.Date;

public class Module {

	private int id;
	private String name;
	private String shortName;
	private Date startDate;
	private Date endDate;
	
	
	public Module(
			int id,
			String name,
			String shortName,
			Date startDate,
			Date endDate)
	{
		this.id = id;
		this.name = name;
		this.shortName = shortName;
		this.startDate = startDate;
		this.endDate = endDate;
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
}
