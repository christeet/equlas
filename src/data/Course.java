package data;

public class Course {

	private int id;
	private String name;
	private String shortName;
	private float weight;
	private Module module;
	private int teacherId;
	
	
	public Course(
			int id,
			String name,
			String shortName,
			float weight,
			Module module,
			int teacherId)
	{
		this.id = id;
		this.name = name;
		this.shortName = shortName;
		this.weight = weight;
		this.module = module;
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
		return module.getId();
	}
	
	public Module getModule() {
		return module;
	}
	
	public int getTeacherId() {
		return teacherId;
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this) return true;
		Course other = (Course) obj;
		if(other == null) return false;
		return other.hashCode() == this.hashCode();
	}

	@Override
	public int hashCode() {
		return this.name.hashCode() + this.id; // very bad hashCode, but it fits our purpose.
	}
}
