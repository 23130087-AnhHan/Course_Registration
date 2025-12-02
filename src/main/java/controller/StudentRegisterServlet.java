package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;
import dao.StudentDao;
import model.*;
import service.*;

@WebServlet("/StudentRegisterServlet")
public class StudentRegisterServlet extends HttpServlet {
    private final CourseClassScheduleService classService = new CourseClassScheduleService();
    private final RegisteredCourseService regService = new RegisteredCourseService();
    private final RegistrationApprovalService approvalService = new RegistrationApprovalService();
    private final ScheduleSuggestionService suggestionService = new ScheduleSuggestionService();
    private final StudentDao studentDao = new StudentDao();

    private static final int DEFAULT_SEMESTER = 2;
    private static final String DEFAULT_YEAR = "2025-2026";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        restoreFlash(req);
        restoreSuggestion(req); // lấy suggestedIds từ session -> build suggestedList

        UserAccount currentUser = (UserAccount) req.getSession().getAttribute("currentUser");
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/LoginServlet");
            return;
        }

        int semester = parseIntOrDefault(req.getParameter("semester"), DEFAULT_SEMESTER);
        String academicYear = strOrDefault(req.getParameter("academicYear"), DEFAULT_YEAR);
        String searchCourse = strOrDefault(req.getParameter("searchCourse"), "");

        String studentId;
        try {
            Student stu = studentDao.getByUserId(currentUser.getUserId());
            if (stu == null) {
                req.setAttribute("error", "Không tìm thấy hồ sơ sinh viên cho tài khoản " + currentUser.getUserId() + ".");
                setFilterAttrs(req, semester, academicYear, searchCourse);
                req.getRequestDispatcher("/views/courses/register_page.jsp").forward(req, resp);
                return;
            }
            studentId = stu.getStudentId();
        } catch (SQLException e) {
            req.setAttribute("error", "Lỗi tải hồ sơ sinh viên: " + e.getMessage());
            setFilterAttrs(req, semester, academicYear, searchCourse);
            req.getRequestDispatcher("/views/courses/register_page.jsp").forward(req, resp);
            return;
        }

        List<CourseClassScheduleDTO> classList =
                classService.getOpenedClasses(semester, academicYear, searchCourse.isBlank() ? null : searchCourse);

        List<RegisteredCourseDTO> registeredList = new ArrayList<>();
        try {
            registeredList = regService.getRegisteredCoursesByStudent(studentId, semester, academicYear);
        } catch (SQLException e) {
            req.setAttribute("error", "Lỗi tải môn đã đăng ký: " + e.getMessage());
        }

        RegistrationApprovalDTO approvalStatus =
                approvalService.getAdvisorApproval(studentId, semester, academicYear);

        setFilterAttrs(req, semester, academicYear, searchCourse);
        req.setAttribute("classList", classList);
        req.setAttribute("registeredList", registeredList);
        req.setAttribute("approvalStatus", approvalStatus);

        req.getRequestDispatcher("/views/courses/register_page.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        UserAccount currentUser = (UserAccount) req.getSession().getAttribute("currentUser");
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/LoginServlet");
            return;
        }

        int semester = parseIntOrDefault(req.getParameter("semester"), DEFAULT_SEMESTER);
        String academicYear = strOrDefault(req.getParameter("academicYear"), DEFAULT_YEAR);
        String searchCourse = strOrDefault(req.getParameter("searchCourse"), "");

        String studentId;
        try {
            Student stu = studentDao.getByUserId(currentUser.getUserId());
            if (stu == null) {
                setFlash(req, "error", "Không tìm thấy hồ sơ sinh viên cho tài khoản " + currentUser.getUserId() + ".");
                redirectWithFilters(resp, req, semester, academicYear, searchCourse, null);
                return;
            }
            studentId = stu.getStudentId();
        } catch (SQLException e) {
            setFlash(req, "error", "Lỗi tải hồ sơ sinh viên: " + e.getMessage());
            redirectWithFilters(resp, req, semester, academicYear, searchCourse, null);
            return;
        }

        String action = req.getParameter("action");
        if ("register".equalsIgnoreCase(action)) {
            String classId = req.getParameter("classId");
            try {
                List<RegisteredCourseDTO> registeredList =
                        regService.getRegisteredCoursesByStudent(studentId, semester, academicYear);

                CourseClassScheduleDTO newClass = classService.getClassWithSchedules(classId);
                if (newClass == null) {
                    setFlash(req, "error", "Không tìm thấy thông tin lớp " + classId);
                    redirectWithFilters(resp, req, semester, academicYear, searchCourse, null);
                    return;
                }

                boolean duplicateCourse = registeredList.stream()
                        .anyMatch(rc -> rc.getCourseId().equalsIgnoreCase(newClass.getCourseId()));
                if (duplicateCourse) {
                    setFlash(req, "error", "Bạn đã đăng ký môn " + newClass.getCourseId() + " trong học kỳ này.");
                    redirectWithFilters(resp, req, semester, academicYear, searchCourse, null);
                    return;
                }

                Integer remaining = newClass.getRemainingNumber();
                if (remaining != null && remaining <= 0) {
                    setFlash(req, "error", "Lớp " + classId + " đã đầy. Vui lòng chọn lớp khác.");
                    redirectWithFilters(resp, req, semester, academicYear, searchCourse, null);
                    return;
                }

                if (hasTimeConflict(newClass, registeredList)) {
                    setFlash(req, "error", "Lịch lớp " + classId + " bị trùng với lịch hiện tại.");
                    redirectWithFilters(resp, req, semester, academicYear, searchCourse, null);
                    return;
                }

                regService.register(studentId, classId, semester, academicYear);
                setFlash(req, "success", "Đăng ký thành công lớp " + classId + "!");
            } catch (SQLException e) {
                setFlash(req, "error", "Không thể đăng ký: " + e.getMessage());
            } catch (Exception e) {
                setFlash(req, "error", "Có lỗi xảy ra: " + e.getMessage());
            }
            redirectWithFilters(resp, req, semester, academicYear, searchCourse, null);
            return;

        } else if ("delete".equalsIgnoreCase(action)) {
            String regIdStr = req.getParameter("regId");
            String classIdParam = req.getParameter("classId");
            try {
                if (regIdStr != null && !regIdStr.isEmpty()) {
                    int regId = Integer.parseInt(regIdStr);
                    regService.deleteRegistration(regId);
                    setFlash(req, "success", "Đã xóa đăng ký (regId=" + regId + ").");
                } else if (classIdParam != null && !classIdParam.isEmpty()) {
                    boolean ok = regService.deleteByStudentAndClass(studentId, classIdParam);
                    if (ok) {
                        setFlash(req, "success", "Đã xóa đăng ký lớp " + classIdParam + ".");
                    } else {
                        setFlash(req, "error", "Không tìm thấy đăng ký để xóa cho lớp " + classIdParam + ".");
                    }
                } else {
                    setFlash(req, "error", "Thiếu thông tin xóa (regId hoặc classId).");
                }
            } catch (Exception e) {
                setFlash(req, "error", "Xóa đăng ký thất bại: " + e.getMessage());
            }
            redirectWithFilters(resp, req, semester, academicYear, searchCourse, "#registered");
            return;

        } else if ("suggest".equalsIgnoreCase(action)) {
            int minCredits = parseIntOrDefault(req.getParameter("minCredits"), 12);
            int maxCredits = parseIntOrDefault(req.getParameter("maxCredits"), 20);

            HillClimbingScheduler.Solution sol =
                    suggestionService.suggest(semester, academicYear,
                            searchCourse.isBlank() ? null : searchCourse,
                            minCredits, maxCredits);

            if (sol.classes == null || sol.classes.isEmpty()) {
                setFlash(req, "error", "Không tìm được gợi ý phù hợp. Hãy nới lỏng ràng buộc hoặc thay bộ lọc.");
            } else {
                // Lưu danh sách classId gợi ý vào session (flash) để hiển thị sau redirect
                List<String> ids = new ArrayList<>();
                for (CourseClassScheduleDTO dto : sol.classes) ids.add(dto.getClassId());
                setSuggestion(req, ids);
                setFlash(req, "success", "Đã tạo gợi ý (" + sol.totalCredits + " TC, score=" + sol.score + ").");
            }
            redirectWithFilters(resp, req, semester, academicYear, searchCourse, "#suggest");
            return;

        } else if ("registerAll".equalsIgnoreCase(action)) {
            // Lấy từ request (nếu gửi kèm) hoặc từ session
            String[] postedIds = req.getParameterValues("classId");
            List<String> ids = new ArrayList<>();
            if (postedIds != null && postedIds.length > 0) {
                ids.addAll(Arrays.asList(postedIds));
            } else {
                ids.addAll(getSuggestion(req));
            }

            if (ids.isEmpty()) {
                setFlash(req, "error", "Không có lớp nào để đăng ký từ gợi ý.");
                redirectWithFilters(resp, req, semester, academicYear, searchCourse, "#suggest");
                return;
            }

            int ok = 0, fail = 0;
            StringBuilder sbErr = new StringBuilder();
            for (String classId : ids) {
                try {
                    // có thể thêm kiểm tra trùng lịch tại đây nếu muốn
                    regService.register(studentId, classId, semester, academicYear);
                    ok++;
                } catch (Exception e) {
                    fail++;
                    if (sbErr.length() < 500) {
                        sbErr.append(classId).append(": ").append(e.getMessage()).append("; ");
                    }
                }
            }
            clearSuggestion(req);

            if (fail == 0) {
                setFlash(req, "success", "Đăng ký tất cả thành công (" + ok + ").");
            } else {
                setFlash(req, "error", "Kết quả: thành công " + ok + ", thất bại " + fail + ". " + sbErr);
            }
            redirectWithFilters(resp, req, semester, academicYear, searchCourse, "#registered");
            return;
        }

        redirectWithFilters(resp, req, semester, academicYear, searchCourse, null);
    }

    private boolean hasTimeConflict(CourseClassScheduleDTO newClass,
                                    List<RegisteredCourseDTO> registeredList) {
        List<ScheduleDetail> newSchedules = newClass.getScheduleList() != null
                ? newClass.getScheduleList()
                : new ArrayList<>();

        for (RegisteredCourseDTO rc : registeredList) {
            List<ScheduleDetail> curSchedules = rc.getScheduleList() != null
                    ? rc.getScheduleList()
                    : new ArrayList<>();

            for (ScheduleDetail a : newSchedules) {
                for (ScheduleDetail b : curSchedules) {
                    if (sameDay(a, b) && overlap(a.getStartPeriod(), a.getEndPeriod(),
                                                 b.getStartPeriod(), b.getEndPeriod())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean sameDay(ScheduleDetail a, ScheduleDetail b) {
        return a.getDayOfWeek() != null && a.getDayOfWeek().equalsIgnoreCase(b.getDayOfWeek());
    }

    private boolean overlap(int s1, int e1, int s2, int e2) {
        return !(e1 < s2 || e2 < s1);
    }

    private int parseIntOrDefault(String s, int def) {
        try { return s == null ? def : Integer.parseInt(s); } catch (Exception e) { return def; }
    }
    private String strOrDefault(String s, String def) { return (s == null) ? def : s.trim(); }

    private void setFilterAttrs(HttpServletRequest req, int semester, String academicYear, String searchCourse) {
        req.setAttribute("semester", semester);
        req.setAttribute("academicYear", academicYear);
        req.setAttribute("searchCourse", searchCourse);
    }

    private void redirectWithFilters(HttpServletResponse resp, HttpServletRequest req,
                                     int semester, String year, String searchCourse, String anchor) throws IOException {
        String base = req.getContextPath() + "/StudentRegisterServlet";
        String url = base +
                "?semester=" + semester +
                "&academicYear=" + enc(year) +
                "&searchCourse=" + enc(searchCourse);
        if (anchor != null) url += anchor;
        resp.sendRedirect(url);
    }

    private String enc(String v) {
        return URLEncoder.encode(v == null ? "" : v, StandardCharsets.UTF_8);
    }

    private void setFlash(HttpServletRequest req, String key, String value) {
        HttpSession session = req.getSession();
        session.setAttribute("flash_" + key, value);
    }

    private void restoreFlash(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return;
        for (String k : List.of("success", "error")) {
            Object val = session.getAttribute("flash_" + k);
            if (val != null) {
                req.setAttribute(k, val);
                session.removeAttribute("flash_" + k);
            }
        }
    }

    private void setSuggestion(HttpServletRequest req, List<String> classIds) {
        HttpSession session = req.getSession();
        session.setAttribute("flash_suggest_ids", classIds);
    }

    private List<String> getSuggestion(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return List.of();
        Object o = session.getAttribute("flash_suggest_ids");
        if (o instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> ids = (List<String>) o;
            return ids;
        }
        return List.of();
    }

    private void clearSuggestion(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) session.removeAttribute("flash_suggest_ids");
    }

    private void restoreSuggestion(HttpServletRequest req) {
        List<String> ids = getSuggestion(req);
        if (ids.isEmpty()) return;

        List<CourseClassScheduleDTO> suggested = new ArrayList<>();
        for (String id : ids) {
            try {
                CourseClassScheduleDTO dto = classService.getClassWithSchedules(id);
                if (dto != null) suggested.add(dto);
            } catch (Exception ignore) {}
        }
        if (!suggested.isEmpty()) {
            req.setAttribute("suggestedList", suggested);
        }
    }
}