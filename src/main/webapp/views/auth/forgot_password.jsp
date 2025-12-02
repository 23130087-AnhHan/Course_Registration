<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Quên mật khẩu</title>
</head>
<body>
<h2>Quên mật khẩu</h2>

<form action="${pageContext.request.contextPath}/ForgotPasswordServlet" method="post">


    Mã sinh viên: <input type="text" name="userId" required><br><br>
    Email: <input type="email" name="email" required><br><br>
    <input type="submit" value="Gửi mã OTP">
</form>

<p style="color:red;">
    ${errorMessage}
</p>
<p style="color:green;">
    ${message}
</p>

<p>
    <a href="${pageContext.request.contextPath}/LoginServlet">Quay lại trang đăng nhập</a>

</p>
</body>
</html>
