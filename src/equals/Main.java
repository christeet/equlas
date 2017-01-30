package equals;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;

import dao.CourseDAO;
import dao.DAOFactory;
import dao.ModuleDAO;
import dao.PersonDAO;
import dao.RatingDAO;
import data.Course;
import data.Module;
import data.Person;
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
		RatingDAO ratingDAO = dao.createRatingDAO();
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
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while(true) {
			try {

				System.out.println("R - set Rating");
				System.out.println("r - get Rating");
				System.out.println("u - get User");
				String command = br.readLine();
				switch (command.charAt(0)){
				case 'R':
					setRating(br, ratingDAO);
					break;
				case 'u':
					getUser(br, personDAO);
					break;
				default:
					System.out.println("no valid input");
					break;
				}
				
				
			}
			catch (Exception e) {
				System.err.println("try again.");
			}
		}
        
	}

	private static void getUser(BufferedReader br, PersonDAO personDAO) throws Exception {
		System.out.println("Username?");
		String username = br.readLine();
		try {
			Person person = personDAO.getPersonByUserName(username);
			System.out.format("Hello, %s %s\r\n", 
					person.getFirstName(), 
					person.getLastName());		
		} catch (SQLException e) {
			System.out.println("Could not find this user!");	
		}
	}
	
	private static void setRating(BufferedReader br, RatingDAO ratingDAO) throws Exception {
		System.out.println("UserId?");
		int userId = Integer.parseInt(br.readLine());
		System.out.println("CourseId?");
		int courseId = Integer.parseInt(br.readLine());
		System.out.println("Rating?");
		int rating = Integer.parseInt(br.readLine());
		try {
			ratingDAO.setRating(userId, courseId, rating);
			System.out.format("User %d has now Rating %d for Course %d\r\n", 
					userId, rating, courseId);	
		} catch (SQLException e) {
			System.out.println(e.getMessage());	
		}
	}

}
