package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import data.Module;
import data.Course;

public class CourseDAO {

	Connection connection;
	
	public CourseDAO(Connection connection) {
		this.connection = connection;
	}
	
	public ArrayList<Course> getCourses(Module module) throws SQLException {
		PreparedStatement ps = connection.prepareStatement
	              ("SELECT c.name, c.shortName, c.weight FROM equals1DB.Course c where c.moduleId in (select m.id from equals1DB.Module m where m.shortName like ?);");
		ps.setString(1, module.getShortName());
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
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
