package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Course;

public class CourseDao extends BaseDao implements IDao<Course> {

	@Override
	public void insert(Course c) throws SQLException {
		// TODO Auto-generated method stub
		String sql="INSERT INTO Course(course_id, course_name, credits, tuition_per_credit, description) VALUES (?, ?, ?, ?, ?)";
		try(Connection con=getConnection();
				PreparedStatement ps=con.prepareStatement(sql)){
			    ps.setString(1, c.getCourseId());
	            ps.setString(2, c.getCourseName());
	            ps.setInt(3, c.getCredits());
	            ps.setDouble(4, c.getTuitionPerCredit());
	            ps.setString(5, c.getDescription());
	            ps.executeUpdate();
		}
		
	}

	@Override
	public void update(Course c) throws SQLException {
		// TODO Auto-generated method stub
		 String sql = "UPDATE Course SET course_name=?, credits=?, tuition_per_credit=?, description=? WHERE course_id=?";
	        try (Connection con = getConnection();
	             PreparedStatement ps = con.prepareStatement(sql)) {
	            ps.setString(1, c.getCourseName());
	            ps.setInt(2, c.getCredits());
	            ps.setDouble(3, c.getTuitionPerCredit());
	            ps.setString(4, c.getDescription());
	            ps.setString(5, c.getCourseId());
	            ps.executeUpdate();
	        }
		
	}

	@Override
	public void delete(String id) throws SQLException {
		// TODO Auto-generated method stub
		String sql = "DELETE FROM Course WHERE course_id=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
		
	}

	@Override
	public Course getById(String id) throws SQLException {
		// TODO Auto-generated method stub
		 String sql = "SELECT * FROM Course WHERE course_id=?";
	        try (Connection con =getConnection();
	             PreparedStatement ps = con.prepareStatement(sql)) {
	            ps.setString(1, id);
	            ResultSet rs = ps.executeQuery();
	            if (rs.next()) {
	                return new Course(
	                    rs.getString("course_id"),
	                    rs.getString("course_name"),
	                    rs.getInt("credits"),
	                    rs.getDouble("tuition_per_credit"),
	                    rs.getString("description")
	                );
	            }
	        }
		return null;
	}

	@Override
	public List<Course> getAll() throws SQLException {
		// TODO Auto-generated method stub
		 List<Course> list = new ArrayList<>();
	        String sql = "SELECT * FROM Course";
	        try (Connection con = getConnection();
	        	PreparedStatement ps = con.prepareStatement(sql);
	             ResultSet rs = ps.executeQuery(sql)) {
	            while (rs.next()) {
	                list.add(new Course(
	                    rs.getString("course_id"),
	                    rs.getString("course_name"),
	                    rs.getInt("credits"),
	                    rs.getDouble("tuition_per_credit"),
	                    rs.getString("description")
	                ));
	            }
	        }
		return list;
	}
	

}
