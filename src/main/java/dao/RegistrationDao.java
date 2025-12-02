package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Registration;

public class RegistrationDao extends BaseDao implements IDao<Registration> {

    @Override
    public void insert(Registration registration) throws SQLException {
        // Bảng Registration dùng snake_case
        String sql = "INSERT INTO Registration (student_id, class_id, reg_date, temp_tuition, status, approved_by, approved_at, note) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, registration.getStudentId());
            ps.setString(2, registration.getClassId());
            ps.setDate(3, registration.getRegDate());
            ps.setDouble(4, registration.getTempTuition());
            ps.setString(5, registration.getStatus());
            ps.setString(6, registration.getApprovedBy());
            ps.setDate(7, registration.getApprovedAt());
            ps.setString(8, registration.getNote());
            ps.executeUpdate();
        }
    }

    @Override
    public void update(Registration registration) throws SQLException {
        String sql = "UPDATE Registration SET student_id=?, class_id=?, reg_date=?, temp_tuition=?, status=?, "
                   + "approved_by=?, approved_at=?, note=? WHERE reg_id=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, registration.getStudentId());
            ps.setString(2, registration.getClassId());
            ps.setDate(3, registration.getRegDate());
            ps.setDouble(4, registration.getTempTuition());
            ps.setString(5, registration.getStatus());
            ps.setString(6, registration.getApprovedBy());
            ps.setDate(7, registration.getApprovedAt());
            ps.setString(8, registration.getNote());
            ps.setInt(9, registration.getRegId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM Registration WHERE reg_id=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(id));
            ps.executeUpdate();
        }
    }

    @Override
    public Registration getById(String id) throws SQLException {
        String sql = "SELECT reg_id, student_id, class_id, reg_date, temp_tuition, status, approved_by, approved_at, note "
                   + "FROM Registration WHERE reg_id=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(id));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Registration> getAll() throws SQLException {
        List<Registration> list = new ArrayList<>();
        String sql = "SELECT reg_id, student_id, class_id, reg_date, temp_tuition, status, approved_by, approved_at, note "
                   + "FROM Registration";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    private Registration mapRow(ResultSet rs) throws SQLException {
        Registration r = new Registration();
        r.setRegId(rs.getInt("reg_id"));
        r.setStudentId(rs.getString("student_id"));
        r.setClassId(rs.getString("class_id"));
        r.setRegDate(rs.getDate("reg_date"));
        r.setTempTuition(rs.getDouble("temp_tuition"));
        r.setStatus(rs.getString("status"));
        r.setApprovedBy(rs.getString("approved_by"));
        r.setApprovedAt(rs.getDate("approved_at"));
        r.setNote(rs.getString("note"));
        return r;
    }

    // Xóa theo studentId + classId (snake_case)
    public boolean deleteByStudentAndClass(String studentId, String classId) throws SQLException {
        String sql = "DELETE FROM Registration WHERE student_id=? AND class_id=? AND status IN ('PENDING','REGISTERED')";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, classId);
            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }
}