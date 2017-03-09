package xml;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import data.Course;
import data.Module;
import data.Rating;
import data.Student;
import persistence.CourseDAO;
import persistence.DAOFactory;
import persistence.ModuleDAO;
import persistence.PersonDAO;
import persistence.RatingDAO;

public class GenerateXML {
	
	private String shortName;
	private ArrayList<Student> students = null;
	private ArrayList<Student> partialStudents = null;
	private ArrayList<Module> modules;
	private Module singleModule = null;

	public GenerateXML(Module module) {
		this.shortName = module.getShortName();
		getModule();
	}
	
	public GenerateXML(String shortName) {
		this.shortName = shortName;
		getModule();
	}
	
	public GenerateXML(ArrayList<Student> partialStudents, Module singleModule) {
		this.partialStudents = partialStudents;
		this.singleModule = singleModule;
		getModule();
	}
	
	private ArrayList<Module> getDAOModuleByName() {
		ModuleDAO moduleDAO = DAOFactory.getInstance().createModuleDAO();
		ArrayList<Module> moduleList = null;
		try {
			moduleList = moduleDAO.getAllModulesByName(this.shortName);
		} catch (SQLException e) {
			System.out.println("Could not get Module from DAOFactory!");
		}
		return moduleList;
	}
	
	private ArrayList<Student> getStudentsByModule(Module module) {
		PersonDAO personDAO = DAOFactory.getInstance().createPersonDAO();
		ArrayList<Student> studentList = null;
		try {
			studentList = personDAO.getStudentsByModule(module);
		} catch (SQLException e) {
			System.out.println("Could not get Persons from DAOFactory!");
		}
		return studentList;
	}
	
	private ArrayList<Course> getCourseByStudentModule(Student student, Module module) {
		CourseDAO courseDAO = DAOFactory.getInstance().createCourseDAO();
		ArrayList<Course> courseList = null;
		try {
			courseList = courseDAO.getCoursesByModuleAndStudent(module, student);
		} catch (SQLException e) {
			System.out.println("Could not get Course from DAOFactory!");
		}
		return courseList;
	}
	
	private Rating getRatingByStudentCourse(Student student, Course course) {
		RatingDAO ratingDAO = DAOFactory.getInstance().createRatingDAO();
		Rating rating = null;
		try {
			rating = ratingDAO.getRating(student.getId(), course.getId());
		} catch (SQLException e) {
			System.out.println("Could not get Rating from DAOFactory!");
		}
		return rating;
	}
	
	private void getModule() {
		if(singleModule == null && partialStudents == null) {
			this.modules = getDAOModuleByName();
		} else {
			this.modules.add(singleModule);
		}
	}
	
	private void getStudents(Module module) {
		if(singleModule == null && partialStudents == null) {
			this.students = getStudentsByModule(module);
		} else {
			this.students = partialStudents;
		}
	}
	
	public void makeXMLDocument() { 
	  try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		
			Document doc = docBuilder.newDocument();
			
			Element studentsTag = doc.createElement("students");
			doc.appendChild(studentsTag);
			
			for(Module m : modules) {
				getStudents(m);
				for(Student student : students) {
					Element studentTag = doc.createElement("student");
					studentsTag.appendChild(studentTag);
					
					Element firstname = doc.createElement("firstName");
					firstname.appendChild(doc.createTextNode(student.getFirstName()));
					studentTag.appendChild(firstname);
				
					Element lastname = doc.createElement("lastName");
					lastname.appendChild(doc.createTextNode(student.getLastName()));
					studentTag.appendChild(lastname);
					
					Element dateofbirth = doc.createElement("dateOfBirth");
					dateofbirth.appendChild(doc.createTextNode(student.getDateOfBirth().toString()));
					studentTag.appendChild(dateofbirth);

					Element placeoforigin = doc.createElement("placeOfOrigin");
					placeoforigin.appendChild(doc.createTextNode(student.getPlaceOfOrigin()));
					studentTag.appendChild(placeoforigin);
					
					Element sex = doc.createElement("sex");
					sex.appendChild(doc.createTextNode(student.getSex()));
					studentTag.appendChild(sex);
					
					Element courseStudentShortName = doc.createElement("shortName");
					courseStudentShortName.appendChild(doc.createTextNode(student.getUserName()));
					studentTag.appendChild(courseStudentShortName);

					for(Course course : getCourseByStudentModule(student, m)) {
						Element coursesTag = doc.createElement("courses");
						studentTag.appendChild(coursesTag);
						
						Element courseName = doc.createElement("name");
						courseName.appendChild(doc.createTextNode(course.getName()));
						coursesTag.appendChild(courseName);
						
						Element courseWeight = doc.createElement("weight");
						courseWeight.appendChild(doc.createTextNode(String.valueOf(course.getWeight())));
						coursesTag.appendChild(courseWeight);
						
						Element courseRating = doc.createElement("rating");
						if(getRatingByStudentCourse(student, course) != null) {
							courseRating.appendChild(doc.createTextNode(
							  String.valueOf(getRatingByStudentCourse(student, course).getSuccessRate()))
							);
						}
						coursesTag.appendChild(courseRating);
					}
					Element moduleTag = doc.createElement("module");
					
					Element moduleName = doc.createElement("name");
					moduleName.appendChild(doc.createTextNode(m.getName()));
					moduleTag.appendChild(moduleName);
					
					Element moduleStartDate = doc.createElement("startDate");
					moduleStartDate.appendChild(doc.createTextNode(String.valueOf(m.getStartDate())));
					moduleTag.appendChild(moduleStartDate);
					
					Element moduleEndDate = doc.createElement("endDate");
					moduleEndDate.appendChild(doc.createTextNode(String.valueOf(m.getEndDate())));
					moduleTag.appendChild(moduleEndDate);
					
					studentTag.appendChild(moduleTag);
				}
			}
				
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("resources/xml/module.xml"));
		
			transformer.transform(source, result);
		
			System.out.println("File saved!");
	
	  } catch (ParserConfigurationException pce) {
		  pce.printStackTrace();
	  } catch (TransformerException tfe) {
		  tfe.printStackTrace();
	  }
	}
}
