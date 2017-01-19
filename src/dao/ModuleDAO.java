package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import data.Module;

public class ModuleDAO {

	Connection connection;
	
	public ModuleDAO(Connection connection) {
		this.connection = connection;
	}
	
	public ArrayList<Module> getAllModules() throws SQLException {
		PreparedStatement ps = connection.prepareStatement
	              ("SELECT * FROM equals1DB.Module;");
	            
		ResultSet rs = null;
		try {
			rs = ps.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ArrayList<Module> resultList = new ArrayList<Module>();
		while (rs != null && rs.next()) {
			String name;
			String shortName;
			Date startDate;
			Date endDate;
			
			try {
				name = rs.getString("name");
				shortName = rs.getString("shortName");
				startDate = rs.getDate("startDate");
				endDate = rs.getDate("endDate");
				
				Module module = new Module(name, shortName, startDate, endDate);
				resultList.add(module);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return resultList;
	}
}
