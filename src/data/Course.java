package data;

public class Course {

	private int id;
	private String name;
	private String shortName;
	private float weight;
	private int moduleId;
	
	
	public Course(
			int id,
			String name,
			String shortName,
			float weight,
			int moduleId)
	{
		this.id = id;
		this.name = name;
		this.shortName = shortName;
		this.weight = weight;
		this.moduleId = moduleId;
	}
	
	public void print() {
		System.out.format("Course %d: %s (%s) has weight %.1f\r\n",getId(), getName(), getShortName(), getWeight());
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
	
	public float getWeight() {
		return weight;
	}
	
	public int getModuleId() {
		return moduleId;
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
}
