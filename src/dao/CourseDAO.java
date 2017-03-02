package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import data.Course;
import data.Module;
import data.Person;

public class CourseDAO {

	private PreparedStatement psGetCoursesByModule;
	private PreparedStatement psGetCoursesByModuleAndTeacher;
	private PreparedStatement psGetCoursesByModuleAndStudent;
	
	public CourseDAO(Connection connection) throws SQLException {
		psGetCoursesByModule = connection.prepareStatement(
				"SELECT c.id, c.name, c.shortName, c.weight, c.moduleId "
				+ "FROM Course c "
				+ "join Module m on c.moduleId = m.id "
				+ "and m.shortName like ?;");
		
		psGetCoursesByModuleAndTeacher = connection.prepareStatement(
				"SELECT c.id, c.name, c.shortName, c.weight, c.moduleId "
				+ "FROM Course c "
				+ "join Module m on c.moduleId = m.id "
				+ "and m.shortName like ?"
				+ "and c.professorId = ?;");
		
		psGetCoursesByModuleAndStudent = connection.prepareStatement(
				"SELECT c.id, c.name, c.shortName, c.weight, c.moduleId "
				+ "FROM Course c "
				+ "join Module m on c.moduleId = m.id "
				+ "join Registration r on m.id = r.moduleId "
				+ "and c.moduleId = ? "
				+ "and r.studentId = ?;");
	}

	public ArrayList<Course> getCoursesByModule(Module module) throws SQLException {
		psGetCoursesByModule.setString(1, module.getShortName());
		ResultSet resultSet = psGetCoursesByModule.executeQuery();
		return getCourseListFromResultSet(resultSet);
	}
	
	public ArrayList<Course> getCoursesByModuleAndTeacher(Module module, Person teacher) throws SQLException {
		psGetCoursesByModuleAndTeacher.setString(1, module.getShortName());
		psGetCoursesByModuleAndTeacher.setInt(2, teacher.getId());
		ResultSet resultSet = psGetCoursesByModuleAndTeacher.executeQuery();
		return getCourseListFromResultSet(resultSet);
	}
	
	public ArrayList<Course> getCoursesByModuleAndStudent(Module module, Person student) throws SQLException {
		psGetCoursesByModuleAndStudent.setInt(1, module.getId());
		psGetCoursesByModuleAndStudent.setInt(2, student.getId());
		ResultSet resultSet = psGetCoursesByModuleAndStudent.executeQuery();
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
				resultSet.getFloat("weight"),
				resultSet.getInt("moduleId"));
	}
	
}
