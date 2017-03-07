package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import data.Module;
import data.Person;
import data.UserRole;

public class ModuleDAO {

	private final String moduleFields = "m.id, m.name, m.shortName, m.startDate, m.endDate, m.headId, m.assistantId";
	private PreparedStatement psGetAllModules;
	private PreparedStatement psGetModulesByStudent;
	private PreparedStatement psGetModulesByAssistant;
	private PreparedStatement psGetModulesByHead;
	private PreparedStatement psGetModulesByTeacher;
	private PreparedStatement psGetAllModulesByName;
	
	public ModuleDAO(Connection connection) throws SQLException {
		psGetAllModules = connection.prepareStatement("SELECT * FROM Module;");
		psGetAllModulesByName = connection.prepareStatement("SELECT * FROM Module where shortname = ?;");
		psGetModulesByStudent = connection.prepareStatement(
				"SELECT distinct " + moduleFields + " FROM Module m "
				+ "left join Registration r on r.moduleId = m.id "
				+ "where r.studentId=?;");
		psGetModulesByAssistant = connection.prepareStatement(
				"SELECT " + moduleFields + " FROM Module m "
				+ "where assistantId=?;");
		psGetModulesByHead = connection.prepareStatement(
				"SELECT " + moduleFields + " FROM Module m "
				+ "where headId=?;");
		
		// only select module, if the teacher is not also the head of the module:
		psGetModulesByTeacher = connection.prepareStatement(
				"SELECT distinct " + moduleFields + " FROM Module m "
				+ "left join Course c on c.moduleId = m.id "
				+ "where c.professorId = ? and m.headId != ?");
	}
	
	public ArrayList<Module> getAllModulesByName(String shortName) throws SQLException {
		psGetAllModulesByName.setString(1, shortName);
		ResultSet resultSet = psGetAllModulesByName.executeQuery();
		return getModuleListFromResultSet(resultSet, null);
	}
	
	public ArrayList<Module> getAllModules() throws SQLException {
		ResultSet resultSet = psGetAllModules.executeQuery();
		return getModuleListFromResultSet(resultSet, null);
	}
	
	public ArrayList<Module> getModulesByAssistant(Person assistant) throws SQLException {
		psGetModulesByAssistant.setInt(1, assistant.getId());
		ResultSet resultSet = psGetModulesByAssistant.executeQuery();
		return getModuleListFromResultSet(resultSet, UserRole.ASSISTANT);
	}
	
	public ArrayList<Module> getModulesByStudent(Person student) throws SQLException {
		psGetModulesByStudent.setInt(1, student.getId());
		ResultSet resultSet = psGetModulesByStudent.executeQuery();
		return getModuleListFromResultSet(resultSet, UserRole.STUDENT);
	}
	
	public ArrayList<Module> getModulesByHead(Person head) throws SQLException {
		psGetModulesByHead.setInt(1, head.getId());
		ResultSet resultSet = psGetModulesByHead.executeQuery();
		return getModuleListFromResultSet(resultSet, UserRole.HEAD);
	}
	
	public ArrayList<Module> getModulesByTeacher(Person teacher) throws SQLException {
		psGetModulesByTeacher.setInt(1, teacher.getId());
		psGetModulesByTeacher.setInt(2, teacher.getId());
		ResultSet resultSet = psGetModulesByTeacher.executeQuery();
		return getModuleListFromResultSet(resultSet, UserRole.TEACHER);
	}
	
	private ArrayList<Module> getModuleListFromResultSet(ResultSet resultSet, UserRole userRole) throws SQLException {
		ArrayList<Module> resultList = new ArrayList<Module>();
		while (resultSet != null && resultSet.next()) {
			Module module = new Module(
					resultSet.getInt("id"), 
					resultSet.getString("name"), 
					resultSet.getString("shortName"), 
					resultSet.getDate("startDate"), 
					resultSet.getDate("endDate"),
					userRole);
			resultList.add(module);
		}
		return resultList;
	}
}
