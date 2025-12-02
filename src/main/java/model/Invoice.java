package model;

import java.sql.Date;

public class Invoice {
	private int invoiceId;
	private String studentId;
	private int semester;
	private String academicYear;
	private int totalCredits;
	private double totalTuition;
	private String paymentStatus;
	private Date paymentDate;
	private Date createdAt;
	public Invoice() {
		super();
	}
	public Invoice(int invoiceId, String studentId, int semester, String academicYear, int totalCredits,
			double totalTuition, String paymentStatus, Date paymentDate, Date createdAt) {
		super();
		this.invoiceId = invoiceId;
		this.studentId = studentId;
		this.semester = semester;
		this.academicYear = academicYear;
		this.totalCredits = totalCredits;
		this.totalTuition = totalTuition;
		this.paymentStatus = paymentStatus;
		this.paymentDate = paymentDate;
		this.createdAt = createdAt;
	}
	public int getInvoiceId() {
		return invoiceId;
	}
	public void setInvoiceId(int invoiceId) {
		this.invoiceId = invoiceId;
	}
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
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
	public int getTotalCredits() {
		return totalCredits;
	}
	public void setTotalCredits(int totalCredits) {
		this.totalCredits = totalCredits;
	}
	public double getTotalTuition() {
		return totalTuition;
	}
	public void setTotalTuition(double totalTuition) {
		this.totalTuition = totalTuition;
	}
	public String getPaymentStatus() {
		return paymentStatus;
	}
	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	public Date getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	

}
