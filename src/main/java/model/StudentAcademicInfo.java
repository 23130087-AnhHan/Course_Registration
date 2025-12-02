package model;

public class StudentAcademicInfo {
	private int academicInfoId;
	private String studentId;
	private String classCode;
	private String major;
	private String specialization;
	private String faculty;
	private String educationLevel;
	private String academicYear;
	public StudentAcademicInfo() {
		super();
	}
	public StudentAcademicInfo(int academicInfoId, String studentId, String classCode, String major,
			String specialization, String faculty, String educationLevel, String academicYear) {
		super();
		this.academicInfoId = academicInfoId;
		this.studentId = studentId;
		this.classCode = classCode;
		this.major = major;
		this.specialization = specialization;
		this.faculty = faculty;
		this.educationLevel = educationLevel;
		this.academicYear = academicYear;
	}
	public int getAcademicInfoId() {
		return academicInfoId;
	}
	public void setAcademicInfoId(int academicInfoId) {
		this.academicInfoId = academicInfoId;
	}
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	public String getClassCode() {
		return classCode;
	}
	public void setClassCode(String classCode) {
		this.classCode = classCode;
	}
	public String getMajor() {
		return major;
	}
	public void setMajor(String major) {
		this.major = major;
	}
	public String getSpecialization() {
		return specialization;
	}
	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}
	public String getFaculty() {
		return faculty;
	}
	public void setFaculty(String faculty) {
		this.faculty = faculty;
	}
	public String getEducationLevel() {
		return educationLevel;
	}
	public void setEducationLevel(String educationLevel) {
		this.educationLevel = educationLevel;
	}
	public String getAcademicYear() {
		return academicYear;
	}
	public void setAcademicYear(String academicYear) {
		this.academicYear = academicYear;
	}
	

}
