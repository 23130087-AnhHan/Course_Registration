package model;

import java.util.List;

/**
 * DTO hiển thị lớp mở + thông tin môn + TKB.
 * remainingNumber dùng Integer để có thể null (tránh nhầm null -> 0).
 */
public class CourseClassScheduleDTO {
    private String classId;
    private String courseId;
    private String courseName;
    private Integer credits;
    private String groupCode;
    private String nestCode;
    private Integer maxCapacity;
    private Integer remainingNumber; // Integer để cho phép null
    private String teacherName;

    // Danh sách TKB cho lớp này
    private List<ScheduleDetail> scheduleList;

    // Getters/Setters
    public String getClassId() { return classId; }
    public void setClassId(String classId) { this.classId = classId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public Integer getCredits() { return credits; }
    public void setCredits(Integer credits) { this.credits = credits; }

    public String getGroupCode() { return groupCode; }
    public void setGroupCode(String groupCode) { this.groupCode = groupCode; }

    public String getNestCode() { return nestCode; }
    public void setNestCode(String nestCode) { this.nestCode = nestCode; }

    public Integer getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(Integer maxCapacity) { this.maxCapacity = maxCapacity; }

    public Integer getRemainingNumber() { return remainingNumber; }
    public void setRemainingNumber(Integer remainingNumber) { this.remainingNumber = remainingNumber; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public List<ScheduleDetail> getScheduleList() { return scheduleList; }
    public void setScheduleList(List<ScheduleDetail> scheduleList) { this.scheduleList = scheduleList; }
}