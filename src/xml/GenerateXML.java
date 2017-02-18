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

import dao.CourseDAO;
import dao.DAOFactory;
import dao.ModuleDAO;
import dao.PersonDAO;
import data.Course;
import data.Module;
import data.Person;
import data.Student;

public class GenerateXML {
	
	private String shortName;
	private Module module;

	public GenerateXML(Module module) {
		this.module = module;
	}
	
	public GenerateXML(String shortName) {
		this.shortName = shortName;
	}
	
	private Person getAssistantByModule(String shortName) {
		PersonDAO personDAO = DAOFactory.getInstance().createPersonDAO();
		Person assistant = null;
		try {
			assistant = personDAO.getPersonByModule(shortName);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("Could not get Assistant from DAOFactory!");
		}
		return assistant;
	}
	
	private ArrayList<Module> getDAOModule() {
		ModuleDAO moduleDAO = DAOFactory.getInstance().createModuleDAO();
		ArrayList<Module> moduleList = null;
		try {
			moduleList = moduleDAO.getAllModules();
		} catch (SQLException e) {
			System.out.println("Could not get Module from DAOFactory!");
		}
		return moduleList;
	}
	
	
	private ArrayList<Course> getDAOCourseByModule(Module module) {
		CourseDAO courseDAO = DAOFactory.getInstance().createCourseDAO();
		ArrayList<Course> courseList = null;
		try {
			courseList = courseDAO.getCoursesByModule(module);
		} catch (SQLException e) {
			System.out.println("Could not get Module from DAOFactory!");
		}
		return courseList;
	}
	
	private Person getTeacherByCourse(Course course) {
		PersonDAO personDAO = DAOFactory.getInstance().createPersonDAO();
		Person teacher = null;
		try {
			teacher = personDAO.getPersonByCourse(course);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("Could not get Teacher from DAOFactory!");
		}
		return teacher;
	}
	
	private ArrayList<Student> getStudentsByCourse(Course course) {
		PersonDAO personDAO = DAOFactory.getInstance().createPersonDAO();
		ArrayList<Student> studentList = null;
		try {
			studentList = personDAO.getStudentsByCourse(course);
		} catch (SQLException e) {
			System.out.println("Could not get Persons from DAOFactory!");
		}
		return studentList;
	}
	
	public void makeXMLDocument() { 
	  try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("module");
			doc.appendChild(rootElement);
			
			for(Module m : getDAOModule()) {
				if(this.shortName.equals(m.getShortName())) {
			
					Element acronym = doc.createElement("acronym");
					acronym.appendChild(doc.createTextNode(m.getShortName()));
					rootElement.appendChild(acronym);
				
					Element assistant = doc.createElement("assistant");
					
					Person moduleAssistant = getAssistantByModule(m.getShortName());
					
					Element assistantFirstName = doc.createElement("firstName");
					assistantFirstName.appendChild(doc.createTextNode(moduleAssistant.getFirstName()));
					Element assistantLastName = doc.createElement("lastName");
					assistantLastName.appendChild(doc.createTextNode(moduleAssistant.getLastName()));
					Element assistantSex = doc.createElement("sex");
					assistantSex.appendChild(doc.createTextNode(moduleAssistant.getSex()));
					Element assistantShortName = doc.createElement("shortName");
					assistantShortName.appendChild(doc.createTextNode(moduleAssistant.getUserName()));
					
					assistant.appendChild(assistantFirstName);
					assistant.appendChild(assistantLastName);
					assistant.appendChild(assistantSex);
					assistant.appendChild(assistantShortName);
					
					rootElement.appendChild(assistant);
					
					for(Course alc : getDAOCourseByModule(m)) {
						
						Element course = doc.createElement("course");
						rootElement.appendChild(course);
						
						Element courseName = doc.createElement("name");
						courseName.appendChild(doc.createTextNode(alc.getName()));
						course.appendChild(courseName);
						
						Person teacher = getTeacherByCourse(alc);
						Element courseProfessor = doc.createElement("professor");
						Element courseProfessorFirstName = doc.createElement("firstName");
						courseProfessorFirstName.appendChild(doc.createTextNode(teacher.getFirstName()));
						Element courseProfessorLastName = doc.createElement("lastName");
						courseProfessorLastName.appendChild(doc.createTextNode(teacher.getLastName()));
						Element courseProfessorSex = doc.createElement("sex");
						courseProfessorSex.appendChild(doc.createTextNode(teacher.getSex()));
						Element courseProfessorShortName = doc.createElement("shortName");
						courseProfessorShortName.appendChild(doc.createTextNode(teacher.getUserName()));
						courseProfessor.appendChild(courseProfessorFirstName);
						courseProfessor.appendChild(courseProfessorLastName);
						courseProfessor.appendChild(courseProfessorSex);
						courseProfessor.appendChild(courseProfessorShortName);
						course.appendChild(courseProfessor);
						
						for(Student student : getStudentsByCourse(alc)) {
							Element rating = doc.createElement("rating");
							course.appendChild(rating);
							
							Element students = doc.createElement("student");
							rating.appendChild(students);
							
							Element dateofbirth = doc.createElement("dateOfBirth");
							dateofbirth.appendChild(doc.createTextNode(student.getDateOfBirth().toString()));
							students.appendChild(dateofbirth);
						
							Element firstname = doc.createElement("firstName");
							firstname.appendChild(doc.createTextNode(student.getFirstName()));
							students.appendChild(firstname);
						
							Element lastname = doc.createElement("lastName");
							lastname.appendChild(doc.createTextNode(student.getLastName()));
							students.appendChild(lastname);

							Element placeoforigin = doc.createElement("placeOfOrigin");
							placeoforigin.appendChild(doc.createTextNode(student.getPlaceOfOrigin()));
							students.appendChild(placeoforigin);
							
							Element sex = doc.createElement("sex");
							sex.appendChild(doc.createTextNode(student.getSex()));
							students.appendChild(sex);
							
							Element courseStudentShortName = doc.createElement("shortName");
							courseStudentShortName.appendChild(doc.createTextNode(student.getUserName()));
							students.appendChild(courseStudentShortName);
						}
						Element courseShortName = doc.createElement("shortName");
						courseShortName.appendChild(doc.createTextNode(alc.getShortName()));
						course.appendChild(courseShortName);
						
						Element courseWeight = doc.createElement("courseWeight");
						courseWeight.appendChild(doc.createTextNode(String.valueOf(alc.getWeight())));
						course.appendChild(courseWeight);
					}
				}
			}
				
				
			
	//			Attr attr = doc.createAttribute("id");
	//			attr.setValue("1");
	//			staff.setAttributeNode(attr);
				
		
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
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
