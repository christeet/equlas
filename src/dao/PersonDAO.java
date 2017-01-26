package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import data.Module;
import data.Student;

public class PersonDAO {

	private PreparedStatement psGetStudentsByModule;
	
	public PersonDAO(Connection connection) throws SQLException {
		psGetStudentsByModule = connection.prepareStatement(
				"SELECT * FROM Person p "
				+ "left join Registration r on r.studentId = p.id "
				+ "left join Module m on r.moduleId = m.id "
				+ "where m.shortName like ? and m.assistantId != p.id and m.headId != p.id;");
	}
	
	public ArrayList<Student> getStudentsByModule(Module module) throws SQLException {
		psGetStudentsByModule.setString(1, module.getShortName());
		ResultSet rs = null;
		try {
			rs = psGetStudentsByModule.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ArrayList<Student> resultList = new ArrayList<Student>();
		while (rs != null && rs.next()) {
			try {
				Student student = new Student(
						rs.getString("firstName"),
						rs.getString("lastName"),
						rs.getString("sex"),
						rs.getString("userName"),
						rs.getString("password"),
						rs.getDate("dateOfBirth"),
						rs.getString("placeOfOrigin"));
				resultList.add(student);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return resultList;
	}
	
}
