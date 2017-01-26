package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import data.Module;
import data.Person;
import data.Student;

public class PersonDAO {

	private PreparedStatement psGetStudentsByModule;
	private PreparedStatement psGetPersonByUsername;
	
	public PersonDAO(Connection connection) throws SQLException {
		psGetStudentsByModule = connection.prepareStatement(
				"SELECT * FROM Person p "
				+ "left join Registration r on r.studentId = p.id "
				+ "left join Module m on r.moduleId = m.id "
				+ "where m.shortName like ? and m.assistantId != p.id and m.headId != p.id;");
		
		psGetPersonByUsername = connection.prepareStatement(
				"SELECT * FROM Person p where p.userName like ?;");
	}
	
	public ArrayList<Student> getStudentsByModule(Module module) throws SQLException {
		psGetStudentsByModule.setString(1, module.getShortName());
		ResultSet resultSet = psGetStudentsByModule.executeQuery();
		return getStudentsListFromResultSet(resultSet);
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
	

	
	private ArrayList<Student> getStudentsListFromResultSet(ResultSet resultSet) throws SQLException{
		ArrayList<Student> resultList = new ArrayList<Student>();
		while (resultSet != null && resultSet.next()) {
			resultList.add(getStudentFromResultSet(resultSet));
		}
		return resultList;
	}
	
	private ArrayList<Person> getPersonsListFromResultSet(ResultSet resultSet) throws SQLException{
		ArrayList<Person> resultList = new ArrayList<Person>();
		while (resultSet != null && resultSet.next()) {
			resultList.add(getPersonFromResultSet(resultSet));
		}
		return resultList;
	}
	
	private Student getStudentFromResultSet(ResultSet resultSet) throws SQLException{
		return new Student(
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
				resultSet.getString("firstName"),
				resultSet.getString("lastName"),
				resultSet.getString("sex"),
				resultSet.getString("userName"),
				resultSet.getString("password"));
	}
}
