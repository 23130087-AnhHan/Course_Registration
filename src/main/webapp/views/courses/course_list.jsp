<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="sectionHeader">Danh sách môn học mở cho đăng ký</div>

<c:if test="${not empty success}">
    <div style="color:#1a7f37;margin:8px 0;">${success}</div>
</c:if>
<c:if test="${not empty error}">
    <div style="color:#d1242f;margin:8px 0;">${error}</div>
</c:if>

<form method="get" action="${pageContext.request.contextPath}/StudentRegisterServlet" style="display:flex; gap:8px; align-items:center; flex-wrap: wrap;">
    <input type="text" name="searchCourse" placeholder="Lọc theo môn học" value="${searchCourse}" />
    <select name="semester">
        <option value="1" ${semester == 1 ? 'selected' : ''}>Học kỳ 1</option>
        <option value="2" ${semester == 2 ? 'selected' : ''}>Học kỳ 2</option>
    </select>
    <select name="academicYear">
        <option value="2025-2026" ${academicYear == '2025-2026' ? 'selected' : ''}>2025-2026</option>
    </select>
    <button type="submit">Lọc</button>
</form>

<!-- Form gợi ý lịch bằng Hill Climbing -->
<form method="post" action="${pageContext.request.contextPath}/StudentRegisterServlet" style="display:flex; gap:8px; align-items:center; margin-top:10px; flex-wrap: wrap;">
    <input type="hidden" name="action" value="suggest"/>
    <input type="hidden" name="searchCourse" value="${searchCourse}"/>
    <input type="hidden" name="semester" value="${semester}"/>
    <input type="hidden" name="academicYear" value="${academicYear}"/>

    <label>Min TC: <input type="number" name="minCredits" value="12" min="0" max="30" style="width:70px"/></label>
    <label>Max TC: <input type="number" name="maxCredits" value="20" min="1" max="30" style="width:70px"/></label>
    <button type="submit">Gợi ý lịch (Hill Climbing)</button>
</form>

<br />

<table>
    <tr>
        <th>Đăng ký</th>
        <th>Mã MH</th>
        <th>Tên môn học</th>
        <th>Nhóm</th>
        <th>Tổ</th>
        <th>Số TC</th>
        <th>Lớp</th>
        <th>Số lượng</th>
        <th>Còn lại</th>
        <th>Giảng viên</th>
        <th>Thời khóa biểu</th>
    </tr>

    <c:forEach var="cl" items="${classList}">
        <tr>
            <td style="text-align:center; white-space:nowrap;">
                <form method="post" action="${pageContext.request.contextPath}/StudentRegisterServlet" style="margin:0;">
                    <input type="hidden" name="action" value="register"/>
                    <input type="hidden" name="classId" value="${cl.classId}"/>
                    <input type="hidden" name="semester" value="${semester}"/>
                    <input type="hidden" name="academicYear" value="${academicYear}"/>
                    <input type="hidden" name="searchCourse" value="${searchCourse}"/>
                    <button type="submit" <c:if test="${cl.remainingNumber != null && cl.remainingNumber <= 0}">disabled</c:if>>
                        Đăng ký
                    </button>
                </form>
            </td>

            <td>${cl.courseId}</td>
            <td>${cl.courseName}</td>
            <td>${cl.groupCode}</td>
            <td>${cl.nestCode}</td>
            <td>${cl.credits}</td>
            <td>${cl.classId}</td>
            <td>${cl.maxCapacity}</td>
            <td>${cl.remainingNumber}</td>
            <td>${cl.teacherName}</td>
            <td>
                <c:forEach var="tkb" items="${cl.scheduleList}">
                    ${tkb.dayOfWeek} tiết ${tkb.startPeriod}-${tkb.endPeriod} phòng ${tkb.room}<br/>
                </c:forEach>
            </td>
        </tr>
    </c:forEach>

    <c:if test="${empty classList}">
        <tr><td colspan="11" style="text-align:center;color:gray;">Không tìm thấy dữ liệu</td></tr>
    </c:if>
</table>