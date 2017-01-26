package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import data.Module;
import data.Student;

public class ModuleDAO {

	private  PreparedStatement psGetAllModules;
	private  PreparedStatement psGetModulesByStudent;
	
	public ModuleDAO(Connection connection) throws SQLException {
		psGetAllModules = connection.prepareStatement("SELECT * FROM equals1DB.Module;");
		psGetModulesByStudent = connection.prepareStatement(
				"SELECT * FROM equals1DB.Module m "
				+ "left join Registration r on r.moduleId = m.id "
				+ "left join Person p on r.studentId = p.id "
				+ "where p.userName like ?;");
	}
	
	public ArrayList<Module> getAllModules() throws SQLException {
		ResultSet resultSet = psGetAllModules.executeQuery();
		return getModuleListFromResultSet(resultSet);
	}
	
	public ArrayList<Module> getModulesByStudent(Student student) throws SQLException {
		psGetModulesByStudent.setString(1, student.getUserName());
		ResultSet resultSet = psGetModulesByStudent.executeQuery();
		return getModuleListFromResultSet(resultSet);
	}
	
	
	
	private ArrayList<Module> getModuleListFromResultSet(ResultSet resultSet) throws SQLException {
		ArrayList<Module> resultList = new ArrayList<Module>();
		while (resultSet != null && resultSet.next()) {
			Module module = new Module(
					resultSet.getString("name"), 
					resultSet.getString("shortName"), 
					resultSet.getDate("startDate"), 
					resultSet.getDate("endDate"));
			resultList.add(module);
		}
		return resultList;
	}
}
