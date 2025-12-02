<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Trang đăng ký môn học</title>
    <style>
        table { border-collapse: collapse; width: 100%; font-size: 14px; margin-bottom: 22px;}
        th, td { border: 1px solid #78e197; padding: 5px;}
        h1, h2 { color: #26b44c;}
        .sectionHeader {font-weight:bold;margin-top:22px;}
    </style>
</head>
<body>
    <h1>ĐĂNG KÝ MÔN HỌC HỌC KỲ ${semester} - NĂM HỌC ${academicYear}</h1>
    <%@ include file="course_list.jsp" %>
    <%@ include file="registered_course.jsp" %>
    <%@ include file="registration_approval.jsp" %>
</body>
</html>