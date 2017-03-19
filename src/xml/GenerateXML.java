package xml;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
import equals.EqualsModel;
import persistence.CourseDAO;
import persistence.DAOFactory;
import persistence.RatingDAO;

public class GenerateXML {
	
	private EqualsModel model;
	private Module module;

	public GenerateXML(EqualsModel model) {
		this.model = model;
		this.module = model.getContextModule();
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
			rating = ratingDAO.getRating(student.getId(), course);
		} catch (SQLException e) {
			throw new Exception("Could not get Rating from DAOFactory!" + e.getMessage());
		}
		return rating;
	}
	
	public void makeXMLDocumentForLeistungsnachweis() throws Exception  {
		makeXMLDocument(model.getStudentsWithCompleteRatingsProperty());
	}
	
	public void makeXMLDocumentForZertifikat() throws Exception {
		makeXMLDocument(model.getStudentsWithGoodGradesProperty());
	}
	
	/**
	 * Generates an XML file for the given list of students.
	 * The XML structure is the same for the Zertifikat and the Leistungsnachweis.
	 * @param students The list of Students for which the Document should be generated
	 * @throws Exception
	 */
	public void makeXMLDocument(List<Person> students) throws Exception { 
	  try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		
			Document doc = docBuilder.newDocument();
			
			Element studentsTag = doc.createElement("students");
			doc.appendChild(studentsTag);
			
			for(Person student : students) {
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

				for(Course course : getCourseByStudentModule(student, module)) {
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
						  String.valueOf(getRatingByStudentCourse(student, course)
								  .getSuccessRate()))
						);
					}
					coursesTag.appendChild(courseRating);
				}
				Element moduleTag = doc.createElement("module");
				
				Element moduleName = doc.createElement("name");
				moduleName.appendChild(doc.createTextNode(module.getName()));
				moduleTag.appendChild(moduleName);
				
				Element moduleStartDate = doc.createElement("startDate");
				moduleStartDate.appendChild(doc.createTextNode(String.valueOf(module.getStartDate())));
				moduleTag.appendChild(moduleStartDate);
				
				Element moduleEndDate = doc.createElement("endDate");
				moduleEndDate.appendChild(doc.createTextNode(String.valueOf(module.getEndDate())));
				moduleTag.appendChild(moduleEndDate);
				
				studentTag.appendChild(moduleTag);
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
