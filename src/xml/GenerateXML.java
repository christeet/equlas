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
import data.Person;
import data.Rating;
import persistence.CourseDAO;
import persistence.DAOFactory;
import persistence.ModuleDAO;
import persistence.PersonDAO;
import persistence.RatingDAO;

public class GenerateXML {
	
	private String shortName;
	private ArrayList<Person> students = null;
	private ArrayList<Person> partialStudents = null;
	private ArrayList<Module> modules;
	private ArrayList<Module> singleModule = null;

	public GenerateXML(Module module) throws Exception {
		System.out.println("XMLGenerate" + module.getShortName());
		this.shortName = module.getShortName();
		getModule();
	}
	
	public GenerateXML(String shortName) throws Exception {
		this.shortName = shortName;
		getModule();
	}
	
	public GenerateXML(ArrayList<Person> partialStudents, ArrayList<Module> singleModule) throws Exception {
		System.out.println("partialStudent: " + partialStudents.toString() + "\n" + singleModule.toString());
		this.partialStudents = partialStudents;
		this.singleModule = singleModule;
		getModule();
	}
	
	private ArrayList<Module> getDAOModuleByName() throws Exception {
		ModuleDAO moduleDAO = DAOFactory.getInstance().createModuleDAO();
		ArrayList<Module> moduleList = null;
		try {
			moduleList = moduleDAO.getAllModulesByName(this.shortName);
		} catch (SQLException e) {
			throw new Exception("Could not get Module from DAOFactory!" + e.getMessage());
		}
		return moduleList;
	}
	
	private ArrayList<Person> getStudentsByModule(Module module) throws Exception {
		PersonDAO personDAO = DAOFactory.getInstance().createPersonDAO();
		ArrayList<Person> studentList = null;
		try {
			studentList = personDAO.getStudentsByModule(module);
		} catch (SQLException e) {
			throw new Exception("Could not get Persons from DAOFactory!" + e.getMessage());
		}
		return studentList;
	}
	
	private ArrayList<Course> getCourseByStudentModule(Person student, Module module) throws Exception {
		CourseDAO courseDAO = DAOFactory.getInstance().createCourseDAO();
		ArrayList<Course> courseList = null;
		try {
			courseList = courseDAO.getCoursesByModuleAndStudent(module, student);
		} catch (SQLException e) {
			throw new Exception("Could not get Course from DAOFactory!" + e.getMessage());
		}
		return courseList;
	}
	
	private Rating getRatingByStudentCourse(Person student, Course course) throws Exception {
		RatingDAO ratingDAO = DAOFactory.getInstance().createRatingDAO();
		Rating rating = null;
		try {
			rating = ratingDAO.getRating(student.getId(), course.getId());
		} catch (SQLException e) {
			throw new Exception("Could not get Rating from DAOFactory!" + e.getMessage());
		}
		return rating;
	}
	
	private void getModule() throws Exception {
		if(singleModule == null && partialStudents == null) {
			System.out.println("singleModule and partialStudents are null!");
			this.modules = getDAOModuleByName();
			System.out.println("Modules: " + modules.get(0).getShortName());
		} else {
			System.out.println("singleModule and partialStudents are set!");
			this.modules = singleModule;
		}
	}
	
	private void getStudents(Module module) throws Exception {
		if(singleModule == null && partialStudents == null) {
			this.students = getStudentsByModule(module);
			System.out.println("Get Students: " + students.size());
		} else {
			this.students = partialStudents;
		}
	}
	
	public void makeXMLDocument() throws Exception { 
	  try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		
			Document doc = docBuilder.newDocument();
			
			Element studentsTag = doc.createElement("students");
			doc.appendChild(studentsTag);
			
			for(Module m : modules) {
				getStudents(m);
				for(Person student : this.students) {
					System.out.println(
						"Student einzeln: " 
						+ student.getFirstName()
						+ "\n" + student.getLastName()
						+ "\n" + student.getDateOfBirth()
						+ "\n" + student.getPlaceOfOrigin()
						+ "\n" + student.getSex()
						+ "\n" + student.getUserName()
					);
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
		  throw new Exception("ParserConfiguration failure! " + pce.getMessage() + "\n" + pce.getStackTrace());
	  } catch (TransformerException tfe) {
		  throw new Exception("TransformerException failure! " + tfe.getMessage() + "\n" + tfe.getStackTrace());
	  }
	}
}
