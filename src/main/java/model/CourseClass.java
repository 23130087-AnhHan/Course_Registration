package model;

public class CourseClass {
	private String classId;
	private String courseId;
	private int semester;
	private String academicYear;
	private String groupCode;
	private String nestCode;
	private String teacherId;
	private int maxCapacity;
	private int remainingNumber;
	private String room;
	private String notes;
	public CourseClass() {
		super();
	}
	public CourseClass(String classId, String courseId, int semester, String academicYear, String groupCode,
			String nestCode, String teacherId, int maxCapacity, int remainingNumber, String room, String notes) {
		super();
		this.classId = classId;
		this.courseId = courseId;
		this.semester = semester;
		this.academicYear = academicYear;
		this.groupCode = groupCode;
		this.nestCode = nestCode;
		this.teacherId = teacherId;
		this.maxCapacity = maxCapacity;
		this.remainingNumber = remainingNumber;
		this.room = room;
		this.notes = notes;
	}
	public String getClassId() {
		return classId;
	}
	public void setClassId(String classId) {
		this.classId = classId;
	}
	public String getCourseId() {
		return courseId;
	}
	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}
	public int getSemester() {
		return semester;
	}
	public void setSemester(int semester) {
		this.semester = semester;
	}
	public String getAcademicYear() {
		return academicYear;
	}
	public void setAcademicYear(String academicYear) {
		this.academicYear = academicYear;
	}
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	public String getNestCode() {
		return nestCode;
	}
	public void setNestCode(String nestCode) {
		this.nestCode = nestCode;
	}
	public String getTeacherId() {
		return teacherId;
	}
	public void setTeacherId(String teacherId) {
		this.teacherId = teacherId;
	}
	public int getMaxCapacity() {
		return maxCapacity;
	}
	public void setMaxCapacity(int maxCapacity) {
		this.maxCapacity = maxCapacity;
	}
	public int getRemainingNumber() {
		return remainingNumber;
	}
	public void setRemainingNumber(int remainingNumber) {
		this.remainingNumber = remainingNumber;
	}
	public String getRoom() {
		return room;
	}
	public void setRoom(String room) {
		this.room = room;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	

}
