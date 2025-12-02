package model;

import java.util.List;


public class RegisteredCourseDTO {
    private int regId;             // Mã đăng ký
    private String courseId;       // Mã môn học
    private String courseName;     // Tên môn học
    private String groupCode;      // Nhóm
    private String nestCode;       // Tổ
    private int credits;           // Số tín chỉ
    private String classId;        // Mã lớp
    private String registerDate;   // Ngày đăng ký (String hoặc Date)
    private String status;         // Trạng thái đăng ký
    private List<ScheduleDetail> scheduleList; // Thời khóa biểu (danh sách tiết/phòng/ngày của lớp này)

    public RegisteredCourseDTO() {}
    
    

    public RegisteredCourseDTO(int regId, String courseId, String courseName, String groupCode, String nestCode,
			int credits, String classId, String registerDate, String status, List<ScheduleDetail> scheduleList) {
		super();
		this.regId = regId;
		this.courseId = courseId;
		this.courseName = courseName;
		this.groupCode = groupCode;
		this.nestCode = nestCode;
		this.credits = credits;
		this.classId = classId;
		this.registerDate = registerDate;
		this.status = status;
		this.scheduleList = scheduleList;
	}



	// Getters & setters
    public int getRegId() { return regId; }
    public void setRegId(int regId) { this.regId = regId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getGroupCode() { return groupCode; }
    public void setGroupCode(String groupCode) { this.groupCode = groupCode; }

    public String getNestCode() { return nestCode; }
    public void setNestCode(String nestCode) { this.nestCode = nestCode; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public String getClassId() { return classId; }
    public void setClassId(String classId) { this.classId = classId; }

    public String getRegisterDate() { return registerDate; }
    public void setRegisterDate(String registerDate) { this.registerDate = registerDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<ScheduleDetail> getScheduleList() { return scheduleList; }
    public void setScheduleList(List<ScheduleDetail> scheduleList) { this.scheduleList = scheduleList; }
}