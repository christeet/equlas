package data;

public class Rating {

	private int studentId;
	private int courseId;
	private int successRate;
	private int version;
	
	
	public Rating(
			int studentId,
			int courseId,
			int successRate,
			int version)
	{
		this.studentId = studentId;
		this.courseId = courseId;
		this.successRate = successRate;
		this.version = version;
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
