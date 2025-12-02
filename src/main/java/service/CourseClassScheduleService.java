package service;

import java.sql.SQLException;
import java.util.List;

import dao.CourseClassScheduleDao;
import model.CourseClassScheduleDTO;

public class CourseClassScheduleService {

    private final CourseClassScheduleDao courseClassDAO = new CourseClassScheduleDao();

    public List<CourseClassScheduleDTO> getOpenedClasses(int semester, String academicYear) {
        try {
            return courseClassDAO.findOpenedClasses(semester, academicYear);
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách lớp mở: " + e.getMessage(), e);
        }
    }

    public CourseClassScheduleDTO getClassWithSchedules(String classId) {
        try {
            return courseClassDAO.findByClassId(classId);
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy thông tin lớp: " + e.getMessage(), e);
        }
    }

    public List<CourseClassScheduleDTO> getOpenedClasses(int semester, String academicYear, String searchKey) {
        try {
            String key = (searchKey == null || searchKey.trim().isEmpty()) ? null : searchKey.trim();
            return courseClassDAO.findOpenedClasses(semester, academicYear, key);
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách lớp mở (có search): " + e.getMessage(), e);
        }
    }
}