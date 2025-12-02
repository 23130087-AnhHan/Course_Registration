package model;

import java.sql.Date;

public class Registration {
	private int regId;
	private String studentId;
	private String classId;
	private Date regDate;
	private double tempTuition;
	private String status;
	private String approvedBy;
	private Date approvedAt;
	private String note;
	public Registration() {
		super();
	}
	public Registration(int regId, String studentId, String classId, Date regDate, double tempTuition, String status,
			String approvedBy, Date approvedAt, String note) {
		super();
		this.regId = regId;
		this.studentId = studentId;
		this.classId = classId;
		this.regDate = regDate;
		this.tempTuition = tempTuition;
		this.status = status;
		this.approvedBy = approvedBy;
		this.approvedAt = approvedAt;
		this.note = note;
	}
	public int getRegId() {
		return regId;
	}
	public void setRegId(int regId) {
		this.regId = regId;
	}
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	public String getClassId() {
		return classId;
	}
	public void setClassId(String classId) {
		this.classId = classId;
	}
	public Date getRegDate() {
		return regDate;
	}
	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}
	public double getTempTuition() {
		return tempTuition;
	}
	public void setTempTuition(double tempTuition) {
		this.tempTuition = tempTuition;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getApprovedBy() {
		return approvedBy;
	}
	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}
	public Date getApprovedAt() {
		return approvedAt;
	}
	public void setApprovedAt(Date approvedAt) {
		this.approvedAt = approvedAt;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}

}
