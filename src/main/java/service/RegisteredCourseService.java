package service;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import dao.CourseClassDao;
import dao.CourseDao;
import dao.RegistrationDao;
import dao.RegisteredCourseDao;
import model.Course;
import model.CourseClass;
import model.Registration;
import model.RegisteredCourseDTO;

public class RegisteredCourseService {

    private final RegistrationDao registrationDao = new RegistrationDao();
    private final CourseClassDao courseClassDao = new CourseClassDao();
    private final CourseDao courseDao = new CourseDao();
    private final RegisteredCourseDao registeredCourseDAO = new RegisteredCourseDao();

    public void register(String studentId, String classId, int semester, String academicYear) throws SQLException {
        CourseClass cc = courseClassDao.getById(classId);
        if (cc == null) throw new SQLException("Không tìm thấy lớp: " + classId);

        Course course = courseDao.getById(cc.getCourseId());
        if (course == null) throw new SQLException("Không tìm thấy môn học: " + cc.getCourseId());

        double tuitionPerCredit = safeDouble(course.getTuitionPerCredit());
        int credits = safeInt(course.getCredits());
        double tempTuition = tuitionPerCredit * credits;

        Registration reg = new Registration();
        reg.setStudentId(studentId);
        reg.setClassId(classId);
        reg.setRegDate(Date.valueOf(LocalDate.now()));
        reg.setTempTuition(tempTuition);
        reg.setStatus("PENDING");
        reg.setApprovedBy(null);
        reg.setApprovedAt(null);
        reg.setNote(null);

        registrationDao.insert(reg);

        // Không giảm Giảm remaining sau đăng ký (nếu không dùng trigger DB)
        //updateRemainingAfterRegister(cc);
    }

    public List<RegisteredCourseDTO> getRegisteredCoursesByStudent(String studentId, int semester, String academicYear) throws SQLException {
        return registeredCourseDAO.getRegisteredCoursesByStudent(studentId, semester, academicYear);
    }

    // remainingNumber là int nên không kiểm tra null, và không cho âm
//    private void updateRemainingAfterRegister(CourseClass cc) throws SQLException {
//        int remaining = cc.getRemainingNumber();          // primitive int
//        int next = Math.max(0, remaining - 1);            // không âm
//        cc.setRemainingNumber(next);
//        courseClassDao.update(cc);
//    }

    // không kiểm tra null, giới hạn không vượt quá maxCapacity nếu có
    public void increaseRemainingByClassId(String classId) throws SQLException {
        CourseClass cc = courseClassDao.getById(classId);
        if (cc != null) {
            int remaining = cc.getRemainingNumber();
            int maxCap = cc.getMaxCapacity();             // giả định int primitive
            int next = (maxCap > 0) ? Math.min(remaining + 1, maxCap) : remaining + 1;
            cc.setRemainingNumber(next);
            courseClassDao.update(cc);
        }
    }

    // Helpers phòng null nếu model Course dùng wrapper; nếu primitive vẫn dùng được (auto-boxing)
    private double safeDouble(Double d) { return d != null ? d : 0.0; }
    private int safeInt(Integer i) { return i != null ? i : 0; }

    // Xóa đăng ký theo regId và tăng lại remaining_number cho lớp tương ứng (nếu không dùng trigger DB)
    public void deleteRegistration(int regId) throws SQLException {
        // Lấy bản ghi để biết classId trước khi xóa
        Registration reg = registrationDao.getById(String.valueOf(regId));
        if (reg == null) {
            // Không có bản ghi để xóa, kết thúc sớm
            return;
        }
        String classId = reg.getClassId();

        // Xóa bản ghi Registration
        registrationDao.delete(String.valueOf(regId));

        // Tăng lại remaining_number cho lớp (nếu bạn không dùng trigger ở DB)
        increaseRemainingByClassId(classId);
    }

    // Tăng lại remaining_number sau khi đã xóa, dựa trên regId
    public void increaseRemainingAfterDelete(int regId) throws SQLException {
        Registration reg = registrationDao.getById(String.valueOf(regId));
        if (reg != null) {
            increaseRemainingByClassId(reg.getClassId());
        }
    }

    // Xóa theo studentId + classId; nếu xóa thành công, tăng lại remaining_number cho lớp
    public boolean deleteByStudentAndClass(String studentId, String classIdParam) {
        try {
            boolean deleted = registrationDao.deleteByStudentAndClass(studentId, classIdParam);
            if (deleted) {
                // tăng lại remaining_number nếu không dùng trigger
                increaseRemainingByClassId(classIdParam);
            }
            return deleted;
        } catch (SQLException e) {
            // Log nếu cần
            e.printStackTrace();
            return false;
        }
    }
}