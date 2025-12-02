package controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.UserAccount;
import service.UserService;
import jakarta.servlet.annotation.WebServlet;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private UserService userService = new UserService();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// HIỂN THỊ TRANG LOGIN ĐÚNG VỊ TRÍ
		request.getRequestDispatcher("views/auth/login.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String userId = request.getParameter("userId");
		String password = request.getParameter("password");

		UserAccount user = userService.login(userId, password);

		if (user != null) {
			HttpSession session = request.getSession();
			session.setAttribute("currentUser", user);

			// ĐI ĐẾN TRANG DASHBOARD
			response.sendRedirect("views/student/dashboard.jsp");

		} else {
			request.setAttribute("errorMessage", "Sai mã sinh viên hoặc mật khẩu");
			request.getRequestDispatcher("views/auth/login.jsp").forward(request, response);
		}
	}
}
