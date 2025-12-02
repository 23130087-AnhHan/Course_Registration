package dao;

import java.sql.*;
import java.util.*;

import model.CourseClassScheduleDTO;
import model.ScheduleDetail;

/**
 * DAO lấy danh sách lớp mở + thông tin môn + thời khóa biểu.
 * Cải tiến:
 * 1. Dùng COALESCE cho remaining_number.
 * 2. Giảm N+1 query: tải toàn bộ schedule của các class trong 1 lần thay vì mỗi lớp 1 query.
 * 3. Bổ sung phương thức hỗ trợ tìm kiếm theo từ khóa mã / tên môn (search).
 * 4. Bổ sung phương thức tìm theo classId (giữ nguyên).
 * 5. Đảm bảo remainingNumber không âm (Math.max).
 * 6. Trả về danh sách schedule theo thứ tự ngày + tiết (ORDER BY đã có).
 */
public class CourseClassScheduleDao extends BaseDao {

    /**
     * Lấy danh sách lớp mở theo học kỳ & năm học (không lọc thêm).
     */
    public List<CourseClassScheduleDTO> findOpenedClasses(int semester, String academicYear) throws SQLException {
        return findOpenedClasses(semester, academicYear, null);
    }

    /**
     * Lấy danh sách lớp mở + cho phép lọc theo từ khoá (mã hoặc tên môn).
     * @param semester học kỳ
     * @param academicYear năm học
     * @param searchKey từ khóa (có thể null) – tìm trong course_id hoặc course_name (LIKE)
     */
    public List<CourseClassScheduleDTO> findOpenedClasses(int semester, String academicYear, String searchKey) throws SQLException {
        List<CourseClassScheduleDTO> list = new ArrayList<>();

        // Ghép điều kiện search (nếu có)
        StringBuilder sql = new StringBuilder(
            "SELECT c.course_id, c.course_name, c.credits, " +
            "       cc.class_id AS classId, cc.group_code AS groupCode, cc.nest_code AS nestCode, " +
            "       cc.max_capacity AS maxCapacity, COALESCE(cc.remaining_number, cc.max_capacity) AS remainingNumber, " +
            "       ua.full_name AS teacher_name " +
            "FROM CourseClass cc " +
            "JOIN Course c ON cc.course_id = c.course_id " +
            "LEFT JOIN UserAccount ua ON ua.user_id = cc.teacher_id " +
            "WHERE cc.semester = ? AND cc.academic_year = ? "
        );

        if (searchKey != null && !searchKey.isBlank()) {
            sql.append("AND (c.course_id LIKE ? OR c.course_name LIKE ?) ");
        }

        sql.append("ORDER BY c.course_id, cc.group_code, cc.nest_code");

        // 1) Lấy danh sách lớp thô
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            ps.setInt(1, semester);
            ps.setString(2, academicYear);

            int paramIndex = 3;
            if (searchKey != null && !searchKey.isBlank()) {
                String like = "%" + searchKey.trim() + "%";
                ps.setString(paramIndex++, like);
                ps.setString(paramIndex, like);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CourseClassScheduleDTO dto = new CourseClassScheduleDTO();
                    dto.setCourseId(rs.getString("course_id"));
                    dto.setCourseName(rs.getString("course_name"));
                    dto.setCredits(rs.getInt("credits"));
                    dto.setClassId(rs.getString("classId"));
                    dto.setGroupCode(rs.getString("groupCode"));
                    dto.setNestCode(rs.getString("nestCode"));
                    dto.setMaxCapacity(rs.getInt("maxCapacity"));
                    dto.setRemainingNumber(
                        Math.max(0, rs.getInt("remainingNumber")) // tránh âm nếu trigger trừ sai
                    );
                    dto.setTeacherName(rs.getString("teacher_name"));
                    list.add(dto);
                }
            }

            // 2) Nếu có lớp, tải lịch học cho tất cả trong 1 query
            if (!list.isEmpty()) {
                Map<String, List<ScheduleDetail>> scheduleMap =
                        loadSchedulesForClasses(con, extractClassIds(list));
                // 3) Gán lịch vào DTO
                for (CourseClassScheduleDTO dto : list) {
                    dto.setScheduleList(scheduleMap.getOrDefault(dto.getClassId(), Collections.emptyList()));
                }
            }
        }
        return list;
    }

    /**
     * Lấy thông tin 1 lớp cùng TKB.
     */
    public CourseClassScheduleDTO findByClassId(String classId) throws SQLException {
        String sql =
            "SELECT c.course_id, c.course_name, c.credits, " +
            "       cc.class_id AS classId, cc.group_code AS groupCode, cc.nest_code AS nestCode, " +
            "       cc.max_capacity AS maxCapacity, COALESCE(cc.remaining_number, cc.max_capacity) AS remainingNumber, " +
            "       ua.full_name AS teacher_name " +
            "FROM CourseClass cc " +
            "JOIN Course c ON cc.course_id = c.course_id " +
            "LEFT JOIN UserAccount ua ON ua.user_id = cc.teacher_id " +
            "WHERE cc.class_id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, classId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CourseClassScheduleDTO dto = new CourseClassScheduleDTO();
                    dto.setCourseId(rs.getString("course_id"));
                    dto.setCourseName(rs.getString("course_name"));
                    dto.setCredits(rs.getInt("credits"));
                    dto.setClassId(rs.getString("classId"));
                    dto.setGroupCode(rs.getString("groupCode"));
                    dto.setNestCode(rs.getString("nestCode"));
                    dto.setMaxCapacity(rs.getInt("maxCapacity"));
                    dto.setRemainingNumber(Math.max(0, rs.getInt("remainingNumber")));
                    dto.setTeacherName(rs.getString("teacher_name"));

                    // Lấy lịch học (1 lớp)
                    dto.setScheduleList(loadSchedulesForClass(con, classId));
                    return dto;
                }
            }
        }
        return null;
    }

    /* ================== Helpers nội bộ ================== */

    private List<String> extractClassIds(List<CourseClassScheduleDTO> dtos) {
        List<String> ids = new ArrayList<>(dtos.size());
        for (CourseClassScheduleDTO dto : dtos) {
            ids.add(dto.getClassId());
        }
        return ids;
    }

    /**
     * Tải lịch học cho nhiều class_id trong một query.
     */
    private Map<String, List<ScheduleDetail>> loadSchedulesForClasses(Connection con, List<String> classIds) throws SQLException {
        Map<String, List<ScheduleDetail>> map = new HashMap<>();
        if (classIds == null || classIds.isEmpty()) return map;

        // Tạo placeholder (?, ?, ?, ...)
        StringBuilder inClause = new StringBuilder();
        inClause.append("(");
        for (int i = 0; i < classIds.size(); i++) {
            if (i > 0) inClause.append(",");
            inClause.append("?");
        }
        inClause.append(")");

        String sql =
            "SELECT schedule_id, class_id, day_of_week, start_period, end_period, room " +
            "FROM ClassSchedule " +
            "WHERE class_id IN " + inClause +
            " ORDER BY FIELD(day_of_week,'Mon','Tue','Wed','Thu','Fri','Sat','Sun'), start_period";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            // bind
            for (int i = 0; i < classIds.size(); i++) {
                ps.setString(i + 1, classIds.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String cid = rs.getString("class_id");
                    ScheduleDetail sd = new ScheduleDetail();
                    sd.setScheduleId(rs.getInt("schedule_id"));
                    sd.setClassId(cid);
                    sd.setDayOfWeek(rs.getString("day_of_week"));
                    sd.setStartPeriod(rs.getInt("start_period"));
                    sd.setEndPeriod(rs.getInt("end_period"));
                    sd.setRoom(rs.getString("room"));

                    map.computeIfAbsent(cid, k -> new ArrayList<>()).add(sd);
                }
            }
        }
        return map;
    }

    /**
     * Tải lịch học cho 1 class_id (dùng riêng trong findByClassId).
     */
    private List<ScheduleDetail> loadSchedulesForClass(Connection con, String classId) throws SQLException {
        List<ScheduleDetail> list = new ArrayList<>();
        String sql =
            "SELECT schedule_id, class_id, day_of_week, start_period, end_period, room " +
            "FROM ClassSchedule WHERE class_id = ? " +
            "ORDER BY FIELD(day_of_week,'Mon','Tue','Wed','Thu','Fri','Sat','Sun'), start_period";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, classId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ScheduleDetail s = new ScheduleDetail();
                    s.setScheduleId(rs.getInt("schedule_id"));
                    s.setClassId(rs.getString("class_id"));
                    s.setDayOfWeek(rs.getString("day_of_week"));
                    s.setStartPeriod(rs.getInt("start_period"));
                    s.setEndPeriod(rs.getInt("end_period"));
                    s.setRoom(rs.getString("room"));
                    list.add(s);
                }
            }
        }
        return list;
    }
}