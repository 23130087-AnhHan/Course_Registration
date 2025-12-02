package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.CourseClass;
import model.CourseClassScheduleDTO;
import model.ScheduleDetail;

public class CourseClassDao extends BaseDao implements IDao<CourseClass> {

    @Override
    public void insert(CourseClass courseClass) throws SQLException {
        String sql = "INSERT INTO CourseClass (class_id, course_id, semester, academic_year, group_code, nest_code, teacher_id, max_capacity, remaining_number, room, notes) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, courseClass.getClassId());
            ps.setString(2, courseClass.getCourseId());
            ps.setInt(3, courseClass.getSemester());
            ps.setString(4, courseClass.getAcademicYear());
            ps.setString(5, courseClass.getGroupCode());
            ps.setString(6, courseClass.getNestCode());
            ps.setString(7, courseClass.getTeacherId());
            ps.setInt(8, courseClass.getMaxCapacity());
            ps.setInt(9, courseClass.getRemainingNumber());
            ps.setString(10, courseClass.getRoom());
            ps.setString(11, courseClass.getNotes());
            ps.executeUpdate();
        }
    }

    @Override
    public void update(CourseClass courseClass) throws SQLException {
        String sql = "UPDATE CourseClass SET course_id=?, semester=?, academic_year=?, group_code=?, nest_code=?, teacher_id=?, "
                   + "max_capacity=?, remaining_number=?, room=?, notes=? WHERE class_id=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, courseClass.getCourseId());
            ps.setInt(2, courseClass.getSemester());
            ps.setString(3, courseClass.getAcademicYear());
            ps.setString(4, courseClass.getGroupCode());
            ps.setString(5, courseClass.getNestCode());
            ps.setString(6, courseClass.getTeacherId());
            ps.setInt(7, courseClass.getMaxCapacity());
            ps.setInt(8, courseClass.getRemainingNumber());
            ps.setString(9, courseClass.getRoom());
            ps.setString(10, courseClass.getNotes());
            ps.setString(11, courseClass.getClassId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM CourseClass WHERE class_id=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public CourseClass getById(String id) throws SQLException {
        String sql = "SELECT * FROM CourseClass WHERE class_id=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<CourseClass> getAll() throws SQLException {
        List<CourseClass> list = new ArrayList<>();
        String sql = "SELECT * FROM CourseClass";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    private CourseClass mapRow(ResultSet rs) throws SQLException {
        return new CourseClass(
            rs.getString("class_id"),
            rs.getString("course_id"),
            rs.getInt("semester"),
            rs.getString("academic_year"),
            rs.getString("group_code"),
            rs.getString("nest_code"),
            rs.getString("teacher_id"),
            rs.getInt("max_capacity"),
            rs.getInt("remaining_number"),
            rs.getString("room"),
            rs.getString("notes")
        );
    }

    // Lấy các lớp mở theo kỳ/năm, kèm thời khóa biểu. Dùng snake_case và alias về tên trường DTO.
    public List<CourseClassScheduleDTO> findOpenedClasses(int semester, String academicYear) throws SQLException {
        List<CourseClassScheduleDTO> list = new ArrayList<>();

        String sql =
            "SELECT c.course_id, c.course_name, c.credits, " +
            "       cc.class_id AS classId, cc.group_code AS groupCode, cc.nest_code AS nestCode, " +
            "       cc.max_capacity AS maxCapacity, cc.remaining_number AS remainingNumber, " +
            "       ua.full_name AS teacher_name " +
            "FROM CourseClass cc " +
            "JOIN Course c ON cc.course_id = c.course_id " +
            "LEFT JOIN UserAccount ua ON cc.teacher_id = ua.user_id " +
            "WHERE cc.semester = ? AND cc.academic_year = ? " +
            "ORDER BY c.course_id, cc.group_code, cc.nest_code";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, semester);
            ps.setString(2, academicYear);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CourseClassScheduleDTO dto = new CourseClassScheduleDTO();
                    dto.setCourseId(rs.getString("course_id"));
                    dto.setCourseName(rs.getString("course_name"));
                    dto.setCredits(rs.getInt("credits"));
                    dto.setClassId(rs.getString("classId"));         // alias
                    dto.setGroupCode(rs.getString("groupCode"));     // alias
                    dto.setNestCode(rs.getString("nestCode"));       // alias
                    dto.setMaxCapacity(rs.getInt("maxCapacity"));    // alias
                    dto.setRemainingNumber(rs.getInt("remainingNumber")); // alias
                    dto.setTeacherName(rs.getString("teacher_name"));

                    // Nạp lịch cho lớp này
                    dto.setScheduleList(getScheduleForClass(con, dto.getClassId()));

                    list.add(dto);
                }
            }
        }

        return list;
    }

    // Tiện ích: lấy thời khóa biểu theo class_id
    private List<ScheduleDetail> getScheduleForClass(Connection con, String classId) throws SQLException {
        List<ScheduleDetail> schedules = new ArrayList<>();
        String scheduleSql =
            "SELECT schedule_id, class_id, day_of_week, start_period, end_period, room " +
            "FROM ClassSchedule WHERE class_id = ? " +
            "ORDER BY FIELD(day_of_week,'Mon','Tue','Wed','Thu','Fri','Sat','Sun'), start_period";

        try (PreparedStatement ps = con.prepareStatement(scheduleSql)) {
            ps.setString(1, classId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ScheduleDetail sd = new ScheduleDetail();
                    sd.setScheduleId(rs.getInt("schedule_id"));
                    sd.setClassId(rs.getString("class_id"));
                    sd.setDayOfWeek(rs.getString("day_of_week"));
                    sd.setStartPeriod(rs.getInt("start_period"));
                    sd.setEndPeriod(rs.getInt("end_period"));
                    sd.setRoom(rs.getString("room"));
                    schedules.add(sd);
                }
            }
        }

        return schedules;
    }
}