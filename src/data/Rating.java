package data;

public class Rating {

	private int studentId;
	private int courseId;
	private int successRate;
	private int version;
	private int moduleId;
	
	
	public Rating(
			int studentId,
			int courseId,
			int successRate,
			int version,
			int moduleId)
	{
		this.studentId = studentId;
		this.courseId = courseId;
		this.successRate = successRate;
		this.version = version;
		this.moduleId = moduleId;
	}
	
	public void print() {
		System.out.format("Student %d has Rating %d in Course %d\r\n",
				studentId, successRate, courseId);
	}

	public int getStudentId() {
		return studentId;
	}
	
	public int getCourseId() {
		return courseId;
	}
	
	public int getModuleId() {
		return moduleId;
	}
	
	public int getSuccessRate() {
		return successRate;
	}
	
	public int setSuccessRate() {
		return successRate;
	}
	
	public int getVersion() {
		return version;
	}
}
