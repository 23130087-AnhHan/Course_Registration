package dao;

import java.sql.*;
import java.util.*;

import model.RegisteredCourseDTO;
import model.ScheduleDetail;

/**
 * DAO: lấy danh sách môn học đã đăng ký của sinh viên.
 * Dùng tên cột snake_case đúng theo schema DB và alias về tên DTO khi cần.
 *
 * Bảng Registration: reg_id, student_id, class_id, reg_date, status
 * Bảng CourseClass: class_id, course_id, group_code, nest_code, semester, academic_year
 * Bảng Course: course_id, course_name, credits
 * Bảng ClassSchedule: class_id, day_of_week, start_period, end_period, room
 */
public class RegisteredCourseDao extends BaseDao {

    public List<RegisteredCourseDTO> getRegisteredCoursesByStudent(String studentId, int semester, String academicYear) throws SQLException {
        List<RegisteredCourseDTO> list = new ArrayList<>();

        String sql =
            "SELECT " +
            "  r.reg_id AS regId, " +
            "  c.course_id, c.course_name, c.credits, " +
            "  cc.class_id AS classId, cc.group_code AS groupCode, cc.nest_code AS nestCode, " +
            "  r.reg_date AS regDate, r.status " +
            "FROM Registration r " +
            "JOIN CourseClass cc ON cc.class_id = r.class_id " +
            "JOIN Course c ON cc.course_id = c.course_id " +
            "WHERE r.student_id = ? AND cc.semester = ? AND cc.academic_year = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setInt(2, semester);
            ps.setString(3, academicYear);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RegisteredCourseDTO dto = new RegisteredCourseDTO();
                    dto.setRegId(rs.getInt("regId"));                 // alias
                    dto.setCourseId(rs.getString("course_id"));
                    dto.setCourseName(rs.getString("course_name"));
                    dto.setCredits(rs.getInt("credits"));
                    dto.setClassId(rs.getString("classId"));          // alias
                    dto.setGroupCode(rs.getString("groupCode"));      // alias
                    dto.setNestCode(rs.getString("nestCode"));        // alias
                    dto.setRegisterDate(String.valueOf(rs.getDate("regDate"))); // alias (đổi sang String nếu cần)
                    dto.setStatus(rs.getString("status"));

                    dto.setScheduleList(getScheduleForClass(con, dto.getClassId()));
                    list.add(dto);
                }
            }
        }
        return list;
    }

    // Lấy lịch học từng lớp theo class_id (snake_case)
    private List<ScheduleDetail> getScheduleForClass(Connection con, String classId) throws SQLException {
        List<ScheduleDetail> schedules = new ArrayList<>();
        String scheduleSql =
            "SELECT day_of_week, start_period, end_period, room " +
            "FROM ClassSchedule WHERE class_id = ? " +
            "ORDER BY FIELD(day_of_week,'Mon','Tue','Wed','Thu','Fri','Sat','Sun'), start_period";

        try (PreparedStatement ps = con.prepareStatement(scheduleSql)) {
            ps.setString(1, classId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ScheduleDetail sd = new ScheduleDetail();
                    sd.setDayOfWeek(rs.getString("day_of_week"));
                    sd.setStartPeriod(rs.getInt("start_period"));
                    sd.setEndPeriod(rs.getInt("end_period"));
                    sd.setRoom(rs.getString("room"));
                    sd.setClassId(classId);
                    schedules.add(sd);
                }
            }
        }
        return schedules;
    }
}