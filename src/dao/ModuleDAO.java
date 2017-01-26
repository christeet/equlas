package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import data.Module;

public class ModuleDAO {

	private  PreparedStatement psGetAllModules;
	
	public ModuleDAO(Connection connection) throws SQLException {
		psGetAllModules = connection.prepareStatement("SELECT * FROM equals1DB.Module;");
	}
	
	public ArrayList<Module> getAllModules() throws SQLException {
		ResultSet rs = null;
		try {
			rs = psGetAllModules.executeQuery();
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
