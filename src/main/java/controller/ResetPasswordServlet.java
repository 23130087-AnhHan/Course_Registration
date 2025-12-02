package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.UserService;
import util.EmailUtils;

import java.io.IOException;
import java.util.Random;

@WebServlet("/ResetPasswordServlet")
public class ResetPasswordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserService userService = new UserService();
    
    // thời gian sống của OTP (5 phút)
    private static final long OTP_EXPIRATION = 5 * 60 * 1000;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Hiển thị trang reset password
        request.getRequestDispatcher("/views/auth/reset_password.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String action = request.getParameter("action"); // sendOtp hoặc resetPassword
        String userId = request.getParameter("userId");

        if ("sendOtp".equals(action)) {
            String email = request.getParameter("email");

            if (userService.checkUserEmail(userId, email)) {
                String otp = String.format("%06d", new Random().nextInt(999999));
                session.setAttribute("otp", otp);
                session.setAttribute("otpTime", System.currentTimeMillis());
                session.setAttribute("otpUserId", userId);

                String subject = "Mã OTP đặt lại mật khẩu";
                String content = "Mã OTP của bạn là: " + otp + ". Mã này có hiệu lực trong 5 phút.";
                boolean sent = EmailUtils.sendEmail(email, subject, content);

                if (sent) {
                    request.setAttribute("message", "Mã OTP đã được gửi đến email của bạn.");
                } else {
                    request.setAttribute("errorMessage", "Gửi email thất bại, vui lòng thử lại sau.");
                }
            } else {
                request.setAttribute("errorMessage", "Mã sinh viên hoặc email không đúng.");
            }
            request.getRequestDispatcher("/views/auth/reset_password.jsp").forward(request, response);

        } else if ("resetPassword".equals(action)) {
            String otpInput = request.getParameter("otp");
            String newPassword = request.getParameter("newPassword");
            String confirmPassword = request.getParameter("confirmPassword");

            String sessionOtp = (String) session.getAttribute("otp");
            Long otpTime = (Long) session.getAttribute("otpTime");
            String otpUserId = (String) session.getAttribute("otpUserId");

            if (sessionOtp == null || otpTime == null || otpUserId == null) {
                request.setAttribute("errorMessage", "OTP không tồn tại, vui lòng thử lại.");
                request.getRequestDispatcher("/views/auth/reset_password.jsp").forward(request, response);
            } else if (!otpInput.equals(sessionOtp)) {
                request.setAttribute("errorMessage", "OTP không đúng.");
                request.getRequestDispatcher("/views/auth/reset_password.jsp").forward(request, response);
            } else if (System.currentTimeMillis() - otpTime > OTP_EXPIRATION) {
                request.setAttribute("errorMessage", "OTP đã hết hạn, vui lòng gửi lại.");
                request.getRequestDispatcher("/views/auth/reset_password.jsp").forward(request, response);
            } else if (!newPassword.equals(confirmPassword)) {
                request.setAttribute("errorMessage", "Mật khẩu xác nhận không khớp.");
                request.getRequestDispatcher("/views/auth/reset_password.jsp").forward(request, response);
            } else {
                boolean updated = userService.updatePassword(otpUserId, newPassword);
                if (updated) {
                    // Xóa OTP khỏi session
                    session.removeAttribute("otp");
                    session.removeAttribute("otpTime");
                    session.removeAttribute("otpUserId");

                    // Chuyển sang login với thông báo
                    request.setAttribute("message", "Đổi mật khẩu thành công! Vui lòng đăng nhập lại.");
                    request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
                } else {
                    request.setAttribute("errorMessage", "Cập nhật mật khẩu thất bại.");
                    request.getRequestDispatcher("/views/auth/reset_password.jsp").forward(request, response);
                }
            }
        }

    }
}
