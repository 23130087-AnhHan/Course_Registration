<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Đăng nhập</title>
</head>
<body>
<h2>Đăng nhập</h2>

<form action="${pageContext.request.contextPath}/LoginServlet" method="post">

    Mã sinh viên: <input type="text" name="userId" required><br><br>
    Mật khẩu: <input type="password" name="password" required><br><br>
    <input type="submit" value="Đăng nhập">
</form>

<p>
    <a href="${pageContext.request.contextPath}/ForgotPasswordServlet">Quên mật khẩu</a>

</p>

<p style="color:red;">
    ${errorMessage}
</p>
<p style="color:green;">
    ${message}
</p>
</body>
</html>
