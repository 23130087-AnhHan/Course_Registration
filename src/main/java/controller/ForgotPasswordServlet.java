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

@WebServlet("/ForgotPasswordServlet")
public class ForgotPasswordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Hiển thị trang quên mật khẩu đúng folder
        request.getRequestDispatcher("/views/auth/forgot_password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String userId = request.getParameter("userId");
        String email = request.getParameter("email");

        if (userService.checkUserEmail(userId, email)) {
            // Tạo OTP 6 số
            String otp = generateOTP();

            // Gửi email
            boolean emailSent = EmailUtils.sendEmail(email,
                    "Mã xác thực quên mật khẩu",
                    "Mã OTP của bạn là: " + otp + "\nHãy nhập mã này để đổi mật khẩu.");

            if (emailSent) {
                // Lưu OTP và userId vào session
                HttpSession session = request.getSession();
                session.setAttribute("otp", otp);
                session.setAttribute("otpUserId", userId);
                session.setAttribute("otpTime", System.currentTimeMillis());

                request.setAttribute("message", "Một mã OTP đã được gửi đến email của bạn.");
                request.getRequestDispatcher("/views/auth/reset_password.jsp").forward(request, response);

            } else {
                request.setAttribute("errorMessage", "Gửi email thất bại, vui lòng thử lại sau.");
                request.getRequestDispatcher("/views/auth/forgot_password.jsp").forward(request, response);
            }
        } else {
            request.setAttribute("errorMessage", "Mã sinh viên hoặc email không đúng.");
            request.getRequestDispatcher("/views/auth/forgot_password.jsp").forward(request, response);
        }
    }

    // Hàm tạo OTP 6 chữ số
    private String generateOTP() {
        Random random = new Random();
        int number = 100000 + random.nextInt(900000); // 100000 -> 999999
        return String.valueOf(number);
    }
}
