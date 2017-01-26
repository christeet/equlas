package data;

public class Course {

	private String name;
	private String shortName;
	private float weight;
	
	
	public Course(
			String name,
			String shortName,
			float weight)
	{
		this.name = name;
		this.shortName = shortName;
		this.weight = weight;
	}
	
	public void print() {
		System.out.format("Course: %s (%s) has weight %.1f\r\n",name, shortName, weight);
	}
	
	public String getName() {
		return name;
	}
	
	public String getShortName() {
		return shortName;
	}
}
