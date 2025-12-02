package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.StudentAcademicInfo;

public class StudentAcademicInfoDao extends BaseDao implements IDao<StudentAcademicInfo>{

	@Override
	public void insert(StudentAcademicInfo info) throws SQLException {
		// TODO Auto-generated method stub
		 String sql = "INSERT INTO StudentAcademicInfo (studentId, classCode, major, specialization, faculty, educationLevel, academicYear) "
                 + "VALUES (?, ?, ?, ?, ?, ?, ?)";
		 try(Connection con= getConnection();
				 PreparedStatement ps=con.prepareStatement(sql)){
			 ps.setString(1, info.getStudentId());
			 ps.setString(2, info.getClassCode());
			 ps.setString(3, info.getMajor());
			 ps.setString(4, info.getSpecialization());
			 ps.setString(5, info.getFaculty());
			 ps.setString(6, info.getEducationLevel());
			 ps.setString(7, info.getAcademicYear());
			 ps.executeUpdate();
		 }
		
	}

	@Override
	public void update(StudentAcademicInfo info) throws SQLException {
		// TODO Auto-generated method stub
		 String sql = "UPDATE StudentAcademicInfo SET studentId=?, classCode=?, major=?, specialization=?, "
                 + "faculty=?, educationLevel=?, academicYear=? WHERE academicInfoId=?";
      try (Connection con = getConnection();
           PreparedStatement ps = con.prepareStatement(sql)) {
    	  ps.setString(1, info.getStudentId());
    	  ps.setString(2, info.getClassCode());
    	  ps.setString(3, info.getMajor());
    	  ps.setString(4, info.getSpecialization());
    	  ps.setString(5, info.getFaculty());
    	  ps.setString(6, info.getEducationLevel());
    	  ps.setString(7, info.getAcademicYear());
    	  ps.setInt(8, info.getAcademicInfoId());
    	  ps.executeUpdate();
      }
		
	}

	@Override
	public void delete(String id) throws SQLException {
		// TODO Auto-generated method stub
		String sql = "DELETE FROM StudentAcademicInfo WHERE academicInfoId=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(id));
            ps.executeUpdate();
        }
		
	}

	@Override
	public StudentAcademicInfo getById(String id) throws SQLException {
		// TODO Auto-generated method stub
		String sql = "SELECT * FROM StudentAcademicInfo WHERE academicInfoId=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
        	ps.setInt(1, Integer.parseInt(id));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        }
		return null;
	}

	@Override
	public List<StudentAcademicInfo> getAll() throws SQLException {
		// TODO Auto-generated method stub
		List<StudentAcademicInfo> list = new ArrayList<>();
        String sql = "SELECT * FROM StudentAcademicInfo";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
		return list;
	}
	private StudentAcademicInfo mapRow(ResultSet rs) throws SQLException {
        return new StudentAcademicInfo(
            rs.getInt("academicInfoId"),
            rs.getString("studentId"),
            rs.getString("classCode"),
            rs.getString("major"),
            rs.getString("specialization"),
            rs.getString("faculty"),
            rs.getString("educationLevel"),
            rs.getString("academicYear")
        );
    }

}
