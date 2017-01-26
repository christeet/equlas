package equals;

import java.sql.SQLException;
import java.util.ArrayList;

import dao.CourseDAO;
import dao.DAOFactory;
import dao.ModuleDAO;
import dao.PersonDAO;
import data.Course;
import data.Module;
import data.Student;

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

		CourseDAO courseDAO = dao.createCourseDAO();
		PersonDAO personDAO = dao.createPersonDAO();
		for(Module m : modules) {
			System.out.println("\r\n------------------------------------------------------------------------------");
			m.print();

			try {
				System.out.println("-- Courses --");
				ArrayList<Course> courses = courseDAO.getCoursesByModule(m);
				for(Course c : courses) {
					c.print();
				}
				System.out.println("-- Students --");
				ArrayList<Student> students = personDAO.getStudentsByModule(m);
				for(Student s : students) {
					s.print();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
	}

}
