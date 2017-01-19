package equals;

import java.sql.SQLException;
import java.util.ArrayList;

import dao.DAOFactory;
import dao.ModuleDAO;
import data.Module;

public class Main {

	
	/**
	 * The entry-point of this program.
	 * @param args commandline arguments
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("testing Database:");
		
		DAOFactory dao = DAOFactory.getInstance();
		ModuleDAO moduleDAO = dao.createModuleDAO();
		ArrayList<Module> modules = null;
		try {
			modules = moduleDAO.getAllModules();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(Module m : modules) {
			m.print();
		}
	}

}
