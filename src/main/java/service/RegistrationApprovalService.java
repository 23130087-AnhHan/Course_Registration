package service;

import dao.RegistrationApprovalDao;
import model.RegistrationApprovalDTO;

public class RegistrationApprovalService {
    private RegistrationApprovalDao dao = new RegistrationApprovalDao();

    public RegistrationApprovalDTO getAdvisorApproval(String studentId, int semester, String academicYear) {
        try {
            return dao.getAdvisorApproval(studentId, semester, academicYear);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}