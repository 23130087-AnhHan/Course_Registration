package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Student;

public class StudentDao extends BaseDao implements IDao<Student> {

	@Override
	public void insert(Student s) throws SQLException {
		// TODO Auto-generated method stub
		String sql="INSERT INTO Student (student_id, user_id, full_name, birth_date, gender, phone_number, id_number, email, place_of_birth, ethnicity, religion, status, household_address) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try(Connection con=getConnection();
				PreparedStatement ps=con.prepareStatement(sql)){
			ps.setString(1, s.getStudentId());
            ps.setString(2, s.getUserId());
            ps.setString(3, s.getFullName());
            ps.setDate(4, s.getBirthDate());
            ps.setString(5, s.getGender());
            ps.setString(6, s.getPhoneNumber());
            ps.setString(7, s.getIdNumber());
            ps.setString(8, s.getEmail());
            ps.setString(9, s.getPlaceOfBirth());
            ps.setString(10, s.getEthnicity());
            ps.setString(11, s.getReligion());
            ps.setString(12, s.getStatus());
            ps.setString(13, s.getHouseholdAddress());
            ps.executeUpdate();
		}
		
	}

	@Override
	public void update(Student s) throws SQLException {
		// TODO Auto-generated method stub
		String sql = "UPDATE Student SET full_name=?, birth_date=?, gender=?, phone_number=?, id_number=?, email=?, place_of_birth=?, ethnicity=?, religion=?, status=?, household_address=? WHERE student_id=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getFullName());
            ps.setDate(2, s.getBirthDate());
            ps.setString(3, s.getGender());
            ps.setString(4, s.getPhoneNumber());
            ps.setString(5, s.getIdNumber());
            ps.setString(6, s.getEmail());
            ps.setString(7, s.getPlaceOfBirth());
            ps.setString(8, s.getEthnicity());
            ps.setString(9, s.getReligion());
            ps.setString(10, s.getStatus());
            ps.setString(11, s.getHouseholdAddress());
            ps.setString(12, s.getStudentId());
            ps.executeUpdate();
        }
		
	}

	@Override
	public void delete(String id) throws SQLException {
		// TODO Auto-generated method stub
		String sql = "DELETE FROM Student WHERE student_id=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
	}

	@Override
	public Student getById(String id) throws SQLException {
		// TODO Auto-generated method stub
		String sql = "SELECT * FROM Student WHERE student_id=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                        rs.getString("student_id"),
                        rs.getString("user_id"),
                        rs.getString("full_name"),
                        rs.getDate("birth_date"),
                        rs.getString("gender"),
                        rs.getString("phone_number"),
                        rs.getString("id_number"),
                        rs.getString("email"),
                        rs.getString("place_of_birth"),
                        rs.getString("ethnicity"),
                        rs.getString("religion"),
                        rs.getString("status"),
                        rs.getString("household_address")
                    );
                }
            }
        }
		return null;
	}

	@Override
	public List<Student> getAll() throws SQLException {
		// TODO Auto-generated method stub
		 List<Student> list = new ArrayList<>();
	        String sql = "SELECT * FROM Student";
	        try (Connection con = getConnection();
	             PreparedStatement ps = con.prepareStatement(sql);
	             ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                list.add(new Student(
	                    rs.getString("student_id"),
	                    rs.getString("user_id"),
	                    rs.getString("full_name"),
	                    rs.getDate("birth_date"),
	                    rs.getString("gender"),
	                    rs.getString("phone_number"),
	                    rs.getString("id_number"),
	                    rs.getString("email"),
	                    rs.getString("place_of_birth"),
	                    rs.getString("ethnicity"),
	                    rs.getString("religion"),
	                    rs.getString("status"),
	                    rs.getString("household_address")
	                ));
	            }
	        }
		return list;
	}
	
	// Thêm: lấy Student theo user_id
    public Student getByUserId(String userId) throws SQLException {
        String sql = "SELECT * FROM Student WHERE user_id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                        rs.getString("student_id"),
                        rs.getString("user_id"),
                        rs.getString("full_name"),
                        rs.getDate("birth_date"),
                        rs.getString("gender"),
                        rs.getString("phone_number"),
                        rs.getString("id_number"),
                        rs.getString("email"),
                        rs.getString("place_of_birth"),
                        rs.getString("ethnicity"),
                        rs.getString("religion"),
                        rs.getString("status"),
                        rs.getString("household_address")
                    );
                }
            }
        }
        return null;
    }

}
