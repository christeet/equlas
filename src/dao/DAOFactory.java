package dao;

import java.sql.Connection;
import java.sql.SQLException;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class DAOFactory {
	
	private Connection connection;
	private static DAOFactory instance;

	/**
	 * Gets the Singleton instance of the DAOFactory or creates it, if not instantiated yet.
	 * @return the one and only instance of DAOFactory.
	 */
	public static DAOFactory getInstance() {
		if(instance == null) {
			instance = new DAOFactory();
		}
		return instance;
	}
	
	private DAOFactory() {
		if(connection == null) {
			try {
				connect();
			} catch (InstantiationException 
					| IllegalAccessException 
					| ClassNotFoundException 
					| SQLException e) {
			  System.out.println(e.getMessage());
			}
		}
	}
	
	private void connect() throws InstantiationException, 
								 IllegalAccessException, 
								 ClassNotFoundException, 
								 SQLException {
    
    Class.forName("com.mysql.jdbc.Driver").newInstance();
    
    MysqlDataSource ds = new MysqlDataSource();
    ds.setServerName("localhost");
    ds.setDatabaseName("equals1DB");
    connection = ds.getConnection("root", "alert");
    
	}
	
	public ModuleDAO createModuleDAO() {
		try {
			return new ModuleDAO(connection);
		} catch (SQLException e) {
			System.out.println("SQL connection was lost");
			return null;
		}
	}
	
	public CourseDAO createCourseDAO() {
		try {
			return new CourseDAO(connection);
		} catch (SQLException e) {
			System.out.println("SQL connection was lost");
			return null;
		}
	}

	public PersonDAO createPersonDAO() {
		try {
			return new PersonDAO(connection);
		} catch (SQLException e) {
			System.out.println("SQL connection was lost");
			return null;
		}
	}

	public RatingDAO createRatingDAO() {
		try {
			return new RatingDAO(connection);
		} catch (SQLException e) {
			System.out.println("SQL connection was lost");
			return null;
		}
	}
  
	public void disconnect() {
	    try {
	        connection.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
}
