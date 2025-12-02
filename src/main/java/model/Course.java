package model;

public class Course {
	private String courseId;
	private String courseName;
	private int credits;
	private double tuitionPerCredit;
	private String description;
	public Course(String courseId, String courseName, int credits, double tuitionPerCredit, String description) {
		super();
		this.courseId = courseId;
		this.courseName = courseName;
		this.credits = credits;
		this.tuitionPerCredit = tuitionPerCredit;
		this.description = description;
	}
	public Course() {
		super();
	}
	public String getCourseId() {
		return courseId;
	}
	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	public int getCredits() {
		return credits;
	}
	public void setCredits(int credits) {
		this.credits = credits;
	}
	public double getTuitionPerCredit() {
		return tuitionPerCredit;
	}
	public void setTuitionPerCredit(double tuitionPerCredit) {
		this.tuitionPerCredit = tuitionPerCredit;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	

}
