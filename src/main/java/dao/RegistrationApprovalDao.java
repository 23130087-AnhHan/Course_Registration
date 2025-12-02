package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.RegistrationApprovalDTO;

/**
 * DAO: lấy kết quả duyệt của cố vấn học tập cho sinh viên
 */
public class RegistrationApprovalDao extends BaseDao {

    public RegistrationApprovalDTO getAdvisorApproval(String studentId, int semester, String academicYear) throws SQLException {
        RegistrationApprovalDTO dto = new RegistrationApprovalDTO();

        String sql = "SELECT ra.statuss as status, ra.message as advisorMessage " +
                "FROM RegistrationApproval ra " +
                "JOIN Registration r ON ra.reg_id = r.reg_id " +
                "JOIN CourseClass cc ON r.class_id = cc.class_id " +
                "WHERE r.student_id = ? AND cc.semester = ? AND cc.academic_year = ? " +
                "ORDER BY ra.approval_date DESC LIMIT 1";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setInt(2, semester);
            ps.setString(3, academicYear);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                dto.setStatus(rs.getString("status"));
                dto.setAdvisorMessage(rs.getString("advisorMessage"));
            }
        }
        return dto;
    }
}