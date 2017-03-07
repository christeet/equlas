package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import data.Course;
import data.Module;
import data.Person;

public class CourseDAO {

	private final String courseFields = "c.id, c.name, c.shortName, c.weight, c.moduleId, c.professorId";
	private PreparedStatement psGetCoursesByModule;
	private PreparedStatement psGetCoursesByModuleAndStudent;
	
	public CourseDAO(Connection connection) throws SQLException {
		psGetCoursesByModule = connection.prepareStatement(
				"SELECT " + courseFields + " FROM Course c "
				+ "join Module m on c.moduleId = m.id "
				+ "and m.shortName like ?;");
		
		psGetCoursesByModuleAndStudent = connection.prepareStatement(
				"SELECT " + courseFields + " FROM Course c "
				+ "join Module m on c.moduleId = m.id "
				+ "join Registration r on m.id = r.moduleId "
				+ "and c.moduleId = ? "
				+ "and r.studentId = ?;");
	}

	public ArrayList<Course> getCoursesByModule(Module module) throws SQLException {
		psGetCoursesByModule.setString(1, module.getShortName());
		ResultSet resultSet = psGetCoursesByModule.executeQuery();
		return getCourseListFromResultSet(resultSet, module);
	}
	
	public ArrayList<Course> getCoursesByModuleAndStudent(Module module, Person student) throws SQLException {
		psGetCoursesByModuleAndStudent.setInt(1, module.getId());
		psGetCoursesByModuleAndStudent.setInt(2, student.getId());
		ResultSet resultSet = psGetCoursesByModuleAndStudent.executeQuery();
		return getCourseListFromResultSet(resultSet, module);
	}
	
	private ArrayList<Course> getCourseListFromResultSet(ResultSet resultSet, Module module) throws SQLException {
		ArrayList<Course> resultList = new ArrayList<Course>();
		while (resultSet != null && resultSet.next()) {
			resultList.add(getCourseFromResultSet(resultSet, module));
		}
		return resultList;
	}
	
	private Course getCourseFromResultSet(ResultSet resultSet, Module module) throws SQLException {
		return new Course(
				resultSet.getInt("id"),
				resultSet.getString("name"), 
				resultSet.getString("shortName"),
				resultSet.getFloat("weight"),
				module,
				resultSet.getInt("professorId"));
	}
	
}
