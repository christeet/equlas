package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import data.Course;
import data.Module;
import data.Person;
import data.Student;

public class PersonDAO {

	private PreparedStatement psGetStudentsByModule;
	private PreparedStatement psGetPersonByModule;
	private PreparedStatement psGetPersonByCourse;
	private PreparedStatement psGetPersonByUsername;
	private PreparedStatement psGetAllUsers;
	
	public PersonDAO(Connection connection) throws SQLException {
		psGetStudentsByModule = connection.prepareStatement(
				"SELECT p.id, p.firstName, p.lastName, p.password, "
				+ "p.sex, p.placeOfOrigin, p.dateOfBirth, p.userName FROM Person p "
        + "left join Registration r on r.studentId = p.id "
        + "left join Module m on r.moduleId = m.id "
        + "left join Course c on m.id = c.moduleId "
        + "left join Rating ra on ra.courseId = c.id and ra.studentId = p.id "
        + "where m.assistantId != p.id and m.headId != p.id and m.shortName like ? "
        + "group by p.id;");
		
		psGetPersonByUsername = connection.prepareStatement(
				"SELECT * FROM Person p where p.userName like ?;");
		
		psGetAllUsers = connection.prepareStatement(
			"Select * from person where id > 200;");
		
		psGetPersonByModule = connection.prepareStatement(
				"SELECT * FROM Person p "
				+ "left join Module m on p.id = m.assistantId "
				+ "where m.shortName like ?");
		
		psGetPersonByCourse = connection.prepareStatement(
				"SELECT * FROM Person p "
				+ "left join Course c on c.professorId = p.id "
				+ "where c.id = ?");
	}
	
	public ArrayList<Student> getAllStudents() throws SQLException {
		ResultSet resultSet = psGetAllUsers.executeQuery();
		return getStudentListFromResultSet(resultSet);
	}
	
	public Person getPersonByModule(String shortName) throws SQLException {
		psGetPersonByModule.setString(1, shortName);
		ResultSet resultSet = psGetPersonByModule.executeQuery();
		if(resultSet.next()) {
			return getPersonFromResultSet(resultSet);
		}
		else {
			throw new SQLException();
		}
	}
	
	public Person getPersonByCourse(Course course) throws SQLException {
		psGetPersonByCourse.setString(1, String.valueOf(course.getId()));
		ResultSet resultSet = psGetPersonByCourse.executeQuery();
		if(resultSet.next()) {
			return getPersonFromResultSet(resultSet);
		}
		else {
			throw new SQLException();
		}
	}
	
	public ArrayList<Person> getStudentsByModule(Module module) throws SQLException {
		psGetStudentsByModule.setString(1, module.getShortName());
		ResultSet resultSet = psGetStudentsByModule.executeQuery();
		return getPersonListFromResultSet(resultSet);
	}
	
	public Person getPersonByUserName(String username) throws SQLException {
		psGetPersonByUsername.setString(1, username);
		ResultSet resultSet = psGetPersonByUsername.executeQuery();
		if(resultSet.next()) {
			return getPersonFromResultSet(resultSet);
		}
		else {
			throw new SQLException();
		}
	}
	
	private ArrayList<Student> getStudentListFromResultSet(ResultSet resultSet) throws SQLException{
		ArrayList<Student> resultList = new ArrayList<Student>();
		while (resultSet != null && resultSet.next()) {
			resultList.add(getStudentFromResultSet(resultSet));
		}
		return resultList;
	}
	
	private ArrayList<Person> getPersonListFromResultSet(ResultSet resultSet) throws SQLException{
		ArrayList<Person> resultList = new ArrayList<Person>();
		while (resultSet != null && resultSet.next()) {
			resultList.add(getPersonFromResultSet(resultSet));
		}
		return resultList;
	}
	
	private Student getStudentFromResultSet(ResultSet resultSet) throws SQLException{
		return new Student(
				resultSet.getInt("id"),
				resultSet.getString("firstName"),
				resultSet.getString("lastName"),
				resultSet.getString("sex"),
				resultSet.getString("userName"),
				resultSet.getString("password"),
				resultSet.getDate("dateOfBirth"),
				resultSet.getString("placeOfOrigin"));
	}
	
	private Person getPersonFromResultSet(ResultSet resultSet) throws SQLException{
		return new Person(
				resultSet.getInt("id"),
				resultSet.getString("firstName"),
				resultSet.getString("lastName"),
				resultSet.getString("sex"),
				resultSet.getString("userName"),
				resultSet.getString("password"),
				resultSet.getDate("dateOfBirth"),
				resultSet.getString("placeOfOrigin"));
	}
}
