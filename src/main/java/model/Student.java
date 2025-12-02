package model;

import java.sql.Date;

public class Student {
	private String studentId;
	private String userId;
	private String fullName;
	private Date birthDate;
	private String gender;
	private String phoneNumber;
	private String idNumber;
	private String email;
	private String placeOfBirth;
	private String ethnicity;
	private String religion;
	private String status;
	private String householdAddress;
	public Student() {
		super();
	}
	public Student(String studentId, String userId, String fullName, Date birthDate, String gender, String phoneNumber,
			String idNumber, String email, String placeOfBirth, String ethnicity, String religion, String status,
			String householdAddress) {
		super();
		this.studentId = studentId;
		this.userId = userId;
		this.fullName = fullName;
		this.birthDate = birthDate;
		this.gender = gender;
		this.phoneNumber = phoneNumber;
		this.idNumber = idNumber;
		this.email = email;
		this.placeOfBirth = placeOfBirth;
		this.ethnicity = ethnicity;
		this.religion = religion;
		this.status = status;
		this.householdAddress = householdAddress;
	}
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public Date getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getIdNumber() {
		return idNumber;
	}
	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPlaceOfBirth() {
		return placeOfBirth;
	}
	public void setPlaceOfBirth(String placeOfBirth) {
		this.placeOfBirth = placeOfBirth;
	}
	public String getEthnicity() {
		return ethnicity;
	}
	public void setEthnicity(String ethnicity) {
		this.ethnicity = ethnicity;
	}
	public String getReligion() {
		return religion;
	}
	public void setReligion(String religion) {
		this.religion = religion;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getHouseholdAddress() {
		return householdAddress;
	}
	public void setHouseholdAddress(String householdAddress) {
		this.householdAddress = householdAddress;
	}
	

}
