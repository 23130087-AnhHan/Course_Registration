<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Đặt lại mật khẩu</title>
</head>
<body>
<h2>Đặt lại mật khẩu</h2>


<!-- Form đổi mật khẩu -->
<form action="${pageContext.request.contextPath}/ResetPasswordServlet" method="post">
    <input type="hidden" name="action" value="resetPassword">
    OTP: <input type="text" name="otp" required><br><br>
    Mật khẩu mới: <input type="password" name="newPassword" required><br><br>
    Xác nhận mật khẩu: <input type="password" name="confirmPassword" required><br><br>
    <input type="submit" value="Đổi mật khẩu">
</form>

<p style="color:red;">${errorMessage}</p>
<p style="color:green;">${message}</p>

<p>
    <a href="${pageContext.request.contextPath}/LoginServlet">Quay lại đăng nhập</a>
</p>
</body>
</html>
