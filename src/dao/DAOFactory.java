package dao;

import java.sql.Connection;
import java.sql.SQLException;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class DAOFactory {
	
	private Connection connection;
	private static DAOFactory instance;

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
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			  System.out.println(e.getMessage());
			}
		}
	}
	
	public void connect() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    
    Class.forName("com.mysql.jdbc.Driver").newInstance();
    
    MysqlDataSource ds = new MysqlDataSource();
    ds.setServerName("localhost");
    ds.setDatabaseName("equals1DB");
    connection = ds.getConnection("root", "alert");
    
	}
	
	public ModuleDAO createModuleDAO() {
		return new ModuleDAO(connection);
	}
  
  public void disconnect() {
    try {
        connection.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
  }
}
