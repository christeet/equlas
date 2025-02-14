package persistence;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class DAOFactory {
	
	private String dbIniFilename = "equalsDatabase.ini";
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
				System.err.println("could not connect to mysql database: ");
				System.out.println(e.getMessage());
			}
		}
	}
	
	private void connect() throws InstantiationException, 
								 IllegalAccessException, 
								 ClassNotFoundException, 
								 SQLException {
		String serverName;
		String dataBaseName;
		String login;
		String password;
		
    	InputStream input = null;
    	try {
    		Properties properties = new Properties();
    		input = new FileInputStream(dbIniFilename);
    		properties.load(input);
    		serverName = properties.getProperty("serverName");
    		dataBaseName = properties.getProperty("dataBaseName");
    		login = properties.getProperty("login");
    		password = properties.getProperty("password");
    	
		    Class.forName("com.mysql.jdbc.Driver").newInstance();
		    
		    MysqlDataSource ds = new MysqlDataSource();
		    ds.setServerName(serverName);
		    ds.setDatabaseName(dataBaseName);
		    connection = ds.getConnection(login, password);
		    System.out.println("Connected!");

    	} catch (IOException ex) {
    		System.err.println("Could not find " + dbIniFilename);
        } finally {
        	if(input!=null) {
        		try {
        			input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	}
        }
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
