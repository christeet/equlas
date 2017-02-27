package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import data.Course;
import data.Module;

public class CourseDAO {

	private PreparedStatement psGetCoursesByModule;
	private PreparedStatement psGetCoursesByStudent;
	private PreparedStatement psGetCourseByStudentModule;
	
	public CourseDAO(Connection connection) throws SQLException {
		psGetCoursesByModule = connection.prepareStatement(
				"SELECT c.id, c.name, c.shortName, c.weight "
				+ "FROM Course c "
				+ "join Module m on c.moduleId = m.id "
				+ "and m.shortName like ?;");
		
		psGetCoursesByStudent = connection.prepareStatement(
				"SELECT c.id, c.name, c.shortName, c.weight "
				+ "FROM Course c "
				+ "join Module m on c.moduleId = m.id "
				+ "join Registration r on r.moduleId = m.id "
				+ "join Person p on p.id = r.studentId "
				+ "and p.id like ?;");
	
	psGetCourseByStudentModule = connection.prepareStatement(
			"SELECT c.id, c.name, c.shortName, c.weight "
			+ "FROM Course c "
			+ "join Module m on c.moduleId = m.id "
			+ "join Registration r on r.moduleId = m.id "
			+ "join Person p on p.id = r.studentId "
			+ "and p.id = ? and m.id = ?;");
  }
	
	public ArrayList<Course> getCourseByStudentModule(int studentId, int moduleId) throws SQLException {
		psGetCourseByStudentModule.setInt(1, studentId);
		psGetCourseByStudentModule.setInt(2, moduleId);
		ResultSet resultSet = psGetCourseByStudentModule.executeQuery();
  	return getCourseListFromResultSet(resultSet);
	}
	
	public ArrayList<Course> getCoursesByModule(Module module) throws SQLException {
		psGetCoursesByModule.setString(1, module.getShortName());
		ResultSet resultSet = psGetCoursesByModule.executeQuery();
		return getCourseListFromResultSet(resultSet);
	}
	
	public ArrayList<Course> getCoursesByStudent(int studentId) throws SQLException {
		psGetCoursesByStudent.setInt(1, studentId);
		ResultSet resultSet = psGetCoursesByStudent.executeQuery();
		return getCourseListFromResultSet(resultSet);
	}
	
	private ArrayList<Course> getCourseListFromResultSet(ResultSet resultSet) throws SQLException {
		ArrayList<Course> resultList = new ArrayList<Course>();
		while (resultSet != null && resultSet.next()) {
			resultList.add(getCourseFromResultSet(resultSet));
		}
		return resultList;
	}
	
	private Course getCourseFromResultSet(ResultSet resultSet) throws SQLException {
		return new Course(
				resultSet.getInt("id"),
				resultSet.getString("name"), 
				resultSet.getString("shortName"),
				resultSet.getFloat("weight"));
	}
	
}
