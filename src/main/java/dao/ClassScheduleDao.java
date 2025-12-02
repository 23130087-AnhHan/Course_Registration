package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.ScheduleDetail;

/**
 * DAO cho bảng ClassSchedule
 * - Cung cấp CRUD theo chuẩn IDao<ScheduleDetail> (theo schedule_id)
 * - Bổ sung phương thức tiện ích findByClassId để lấy toàn bộ các dòng TKB của 1 lớp
 */
public class ClassScheduleDao  extends BaseDao implements IDao<ScheduleDetail> {

    @Override
    public void insert(ScheduleDetail s) throws SQLException {
        String sql = "INSERT INTO ClassSchedule (class_id, day_of_week, start_period, end_period, room) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getClassId());
            ps.setString(2, s.getDayOfWeek());
            ps.setInt(3, s.getStartPeriod());
            ps.setInt(4, s.getEndPeriod());
            ps.setString(5, s.getRoom());
            ps.executeUpdate();
        }
    }

    @Override
    public void update(ScheduleDetail s) throws SQLException {
        String sql = "UPDATE ClassSchedule SET class_id=?, day_of_week=?, start_period=?, end_period=?, room=? "
                   + "WHERE schedule_id=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getClassId());
            ps.setString(2, s.getDayOfWeek());
            ps.setInt(3, s.getStartPeriod());
            ps.setInt(4, s.getEndPeriod());
            ps.setString(5, s.getRoom());
            ps.setInt(6, s.getScheduleId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM ClassSchedule WHERE schedule_id=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(id));
            ps.executeUpdate();
        }
    }

    @Override
    public ScheduleDetail getById(String id) throws SQLException {
        String sql = "SELECT schedule_id, class_id, day_of_week, start_period, end_period, room "
                   + "FROM ClassSchedule WHERE schedule_id=?";
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
    public List<ScheduleDetail> getAll() throws SQLException {
        List<ScheduleDetail> list = new ArrayList<>();
        String sql = "SELECT schedule_id, class_id, day_of_week, start_period, end_period, room "
                   + "FROM ClassSchedule "
                   + "ORDER BY class_id, day_of_week, start_period";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    /**
     * Tiện ích: lấy toàn bộ các dòng thời khóa biểu theo class_id
     */
    public List<ScheduleDetail> findByClassId(String classId) throws SQLException {
        List<ScheduleDetail> list = new ArrayList<>();
        String sql = "SELECT schedule_id, class_id, day_of_week, start_period, end_period, room "
                   + "FROM ClassSchedule WHERE class_id=? "
                   + "ORDER BY FIELD(day_of_week,'Mon','Tue','Wed','Thu','Fri','Sat','Sun'), start_period";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, classId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    private ScheduleDetail mapRow(ResultSet rs) throws SQLException {
        ScheduleDetail s = new ScheduleDetail();
        s.setScheduleId(rs.getInt("schedule_id"));
        s.setClassId(rs.getString("class_id"));
        s.setDayOfWeek(rs.getString("day_of_week"));
        s.setStartPeriod(rs.getInt("start_period"));
        s.setEndPeriod(rs.getInt("end_period"));
        s.setRoom(rs.getString("room"));
        return s;
    }
}