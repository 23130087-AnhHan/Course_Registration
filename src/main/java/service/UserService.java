package service;

import java.sql.SQLException;
import java.util.Random;

import dao.UserAccountDao;
import model.UserAccount;
import util.EmailUtils;

public class UserService {

    private UserAccountDao userDao = new UserAccountDao();
    private static final int OTP_LENGTH = 6;

    // ============================
    // LOGIN
    // ============================
    public UserAccount login(String userId, String password) {

        if (userId == null || password == null) return null;

        try {
            UserAccount user = userDao.getById(userId.trim());

            if (user != null) {
                // Tạm thời: so sánh trực tiếp password
                if (user.getPasswordHash().equals(password.trim())) {
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ============================
    // CHECK EMAIL
    // ============================
    public boolean checkUserEmail(String userId, String email) {
        try {
            if (userId == null || email == null) return false;

            UserAccount user = userDao.getById(userId.trim());

            return user != null && user.getEmail().equalsIgnoreCase(email.trim());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ============================
    // UPDATE PASSWORD
    // ============================
    public boolean updatePassword(String userId, String newPassword) {
        try {
            if (userId == null || newPassword == null) return false;

            UserAccount user = userDao.getById(userId);

            if (user != null) {
                user.setPasswordHash(newPassword.trim());
                userDao.update(user);
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ============================
    // TẠO OTP
    // ============================
    private String generateOTP() {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < OTP_LENGTH; i++) {
            sb.append(r.nextInt(10));
        }
        return sb.toString();
    }

    // ============================
    // GỬI OTP
    // ============================
    public String sendOTPToEmail(String userId, String email) {

        // BẮT BUỘC kiểm tra user + email
        if (!checkUserEmail(userId, email)) {
            return null; 
        }

        String otp = generateOTP();

        String subject = "Mã OTP lấy lại mật khẩu";
        String message = "Mã OTP của bạn là: " + otp;

        boolean sent = EmailUtils.sendEmail(email, subject, message);

        if (sent) {
            return otp; // servlet sẽ lưu vào session
        }

        return null;
    }
}
