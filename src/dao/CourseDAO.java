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
	
	public CourseDAO(Connection connection) throws SQLException {
		psGetCoursesByModule = connection.prepareStatement(
				"SELECT c.name, c.shortName, c.weight "
				+ "FROM equals1DB.Course c "
				+ "join equals1DB.Module m on c.moduleId = m.id "
				+ "and m.shortName like ?;");
	}
	
	public ArrayList<Course> getCoursesByModule(Module module) throws SQLException {
		psGetCoursesByModule.setString(1, module.getShortName());
		ResultSet rs = null;
		try {
			rs = psGetCoursesByModule.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ArrayList<Course> resultList = new ArrayList<Course>();
		while (rs != null && rs.next()) {
			String name;
			String shortName;
			float weight;
			
			try {
				name = rs.getString("name");
				shortName = rs.getString("shortName");
				weight = rs.getFloat("weight");
				
				Course course = new Course(name, shortName, weight);
				resultList.add(course);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return resultList;
	}
	
}
