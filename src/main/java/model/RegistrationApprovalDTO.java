package model;

/**
 * DTO chứa thông tin kết quả duyệt đăng ký của cố vấn học tập
 */
public class RegistrationApprovalDTO {
    private String status;          // Trạng thái duyệt (Pending, Approved, Rejected)
    private String advisorMessage;  // Lời nhắn của cố vấn cho sinh viên

    public RegistrationApprovalDTO() {}
    
    

    public RegistrationApprovalDTO(String status, String advisorMessage) {
		super();
		this.status = status;
		this.advisorMessage = advisorMessage;
	}



	public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdvisorMessage() {
        return advisorMessage;
    }
    public void setAdvisorMessage(String advisorMessage) {
        this.advisorMessage = advisorMessage;
    }
}