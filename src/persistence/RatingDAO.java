package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import data.Course;
import data.Module;
import data.Rating;

public class RatingDAO {

	private String ratingColumns = "studentId, courseId, successRate, version";
	private PreparedStatement psGetAllRatingsForCourse;
	private PreparedStatement psGetAllRatingsForStudent;
	private PreparedStatement psGetRating;
	private PreparedStatement psInsertRating;
	private PreparedStatement psUpdateRating;
	private PreparedStatement psRemoveRating;
	
	public RatingDAO(Connection connection) throws SQLException {
		psGetAllRatingsForCourse = connection.prepareStatement(
				"SELECT " + ratingColumns + " FROM Rating WHERE courseId=?");
		
		psGetAllRatingsForStudent = connection.prepareStatement(
				"SELECT " + ratingColumns + " FROM Rating WHERE studentId=?");
		
		psGetRating = connection.prepareStatement(
				"SELECT " + ratingColumns + " FROM Rating WHERE studentId=? AND courseId=?");

		psInsertRating = connection.prepareStatement(
				"INSERT INTO Rating (studentId, courseId, successRate, version) VALUES"
				+ "(?, "
				+ "?, "
				+ "?, "
				+ "0);");
		
		psUpdateRating = connection.prepareStatement(
				"UPDATE Rating r SET "
				+ "r.successRate=?, "
				+ "r.version=? "
				+ "WHERE "
				+ "r.studentId=? "
				+ "AND r.courseId=? "
				+ "AND version=?");

		psRemoveRating = connection.prepareStatement(
				"DELETE FROM Rating WHERE studentId=? AND courseId=?");
		
	}
	
	public Rating setRating(int studentId, Course course, int successRate) throws SQLException {
		Rating existingRating = getRating(studentId, course);
		if(existingRating == null) {
			insertRating(studentId, course.getId(), successRate);
		}
		else {
			updateRating(existingRating, successRate);
		}
		return getRating(studentId, course);
	}

	public int removeRating(int studentId, Course course) throws SQLException {
		psRemoveRating.setInt(1, studentId);
		psRemoveRating.setInt(2, course.getId());
		int nbrOfModifiedRecords = psRemoveRating.executeUpdate();
		return nbrOfModifiedRecords;
	}
	
	
	public Rating getRating(int studentId, Course course) throws SQLException {
		psGetRating.setInt(1, studentId);
		psGetRating.setInt(2, course.getId());
		ResultSet resultSet = psGetRating.executeQuery();
		if(resultSet.next()) {
			return getRatingFromResultSet(resultSet, course.getModuleId());
		}
		else {
			return null;
		}
	}
	
	public ArrayList<Rating> getRatingListForCourse(Course course) throws SQLException {
		psGetAllRatingsForCourse.setInt(1, course.getId());
		ResultSet resultSet = psGetAllRatingsForCourse.executeQuery();
		return getRatingListFromResultSet(resultSet, course.getModuleId());
	}
	
	public ArrayList<Rating> getRatingListForStudent(int studentId, Module module) throws SQLException {
		psGetAllRatingsForStudent.setInt(1, studentId);
		ResultSet resultSet = psGetAllRatingsForStudent.executeQuery();
		return getRatingListFromResultSet(resultSet, module.getId());
	}
	
	private Rating getRatingFromResultSet(ResultSet resultSet, int moduleId) throws SQLException{
		return new Rating(
				resultSet.getInt("studentId"),
				resultSet.getInt("courseId"),
				resultSet.getInt("successRate"),
				resultSet.getInt("version"),
				moduleId);
	}
	

	private ArrayList<Rating> getRatingListFromResultSet(ResultSet resultSet, int moduleId) throws SQLException{
		ArrayList<Rating> resultList = new ArrayList<Rating>();
		while (resultSet != null && resultSet.next()) {
			resultList.add(getRatingFromResultSet(resultSet, moduleId));
		}
		return resultList;
	}
	
	private void insertRating(int studentId, int courseId, int successRate) throws SQLException {
		psInsertRating.setInt(1, studentId);
		psInsertRating.setInt(2, courseId);
		psInsertRating.setInt(3, successRate);
		psInsertRating.executeUpdate();  
	}
	
	private void updateRating(Rating existingRating, int newSuccessRate) throws SQLException {
		psUpdateRating.setInt(1, newSuccessRate);
		psUpdateRating.setInt(2, existingRating.getVersion() + 1); // new version (increment of previous version)
		psUpdateRating.setInt(3, existingRating.getStudentId());
		psUpdateRating.setInt(4, existingRating.getCourseId());
		psUpdateRating.setInt(5, existingRating.getVersion()); // previous version
		
		int nbrOfModifiedRecords = psUpdateRating.executeUpdate();  
		if (nbrOfModifiedRecords != 1) {
			throw new SQLException("setting Rating failed!");
		}
	}
}
