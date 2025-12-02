<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<div class="sectionHeader">Danh sách môn học đã đăng ký</div>

<c:if test="${not empty success}">
    <div style="color:#1a7f37;margin:8px 0;">${success}</div>
</c:if>
<c:if test="${not empty error}">
    <div style="color:#d1242f;margin:8px 0;">${error}</div>
</c:if>

<table id="registered">
    <tr>
        <th>Xóa</th>
        <th>Mã MH</th>
        <th>Tên môn học</th>
        <th>Nhóm tổ</th>
        <th>Số TC</th>
        <th>Lớp</th>
        <th>Ngày đăng ký</th>
        <th>Trạng thái</th>
        <th>Thời khóa biểu</th>
    </tr>
    <c:forEach var="rc" items="${registeredList}">
        <tr>
            <td style="text-align:center;">
                <form method="post" action="${pageContext.request.contextPath}/StudentRegisterServlet" style="margin:0;">
                    <input type="hidden" name="action" value="delete"/>
                    <input type="hidden" name="regId" value="${rc.regId}"/>
                    <input type="hidden" name="semester" value="${semester}"/>
                    <input type="hidden" name="academicYear" value="${academicYear}"/>
                    <input type="hidden" name="searchCourse" value="${searchCourse}"/>
                    <button type="submit" onclick="return confirm('Bạn chắc chắn xóa đăng ký lớp ${rc.classId}?');">❌</button>
                </form>
            </td>
            <td>${rc.courseId}</td>
            <td>${rc.courseName}</td>
            <td>${rc.groupCode}-${rc.nestCode}</td>
            <td>${rc.credits}</td>
            <td>${rc.classId}</td>
            <td>${rc.registerDate}</td>
            <td>${rc.status}</td>
            <td>
                <c:forEach var="tkb" items="${rc.scheduleList}">
                    ${tkb.dayOfWeek} tiết ${tkb.startPeriod}-${tkb.endPeriod} phòng ${tkb.room}<br/>
                </c:forEach>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty registeredList}">
        <tr><td colspan="9" style="text-align:center;color:gray;">Không tìm thấy dữ liệu</td></tr>
    </c:if>
</table>

<!-- Khu vực gợi ý lịch -->
<div id="suggest" class="sectionHeader" style="margin-top:18px;">Gợi ý lịch tối ưu</div>
<c:if test="${not empty suggestedList}">
    <form method="post" action="${pageContext.request.contextPath}/StudentRegisterServlet" style="margin-bottom:10px;">
        <input type="hidden" name="action" value="registerAll"/>
        <input type="hidden" name="semester" value="${semester}"/>
        <input type="hidden" name="academicYear" value="${academicYear}"/>
        <input type="hidden" name="searchCourse" value="${searchCourse}"/>
        <!-- Gửi classId[] để phòng trường hợp session mất -->
        <c:forEach var="s" items="${suggestedList}">
            <input type="hidden" name="classId" value="${s.classId}"/>
        </c:forEach>
        <button type="submit">Đăng ký tất cả từ gợi ý</button>
    </form>

    <table>
        <tr>
            <th>Mã MH</th>
            <th>Tên môn học</th>
            <th>Nhóm</th>
            <th>Tổ</th>
            <th>Số TC</th>
            <th>Lớp</th>
            <th>Giảng viên</th>
            <th>Thời khóa biểu</th>
        </tr>
        <c:forEach var="s" items="${suggestedList}">
            <tr>
                <td>${s.courseId}</td>
                <td>${s.courseName}</td>
                <td>${s.groupCode}</td>
                <td>${s.nestCode}</td>
                <td>${s.credits}</td>
                <td>${s.classId}</td>
                <td>${s.teacherName}</td>
                <td>
                    <c:forEach var="tkb" items="${s.scheduleList}">
                        ${tkb.dayOfWeek} tiết ${tkb.startPeriod}-${tkb.endPeriod} phòng ${tkb.room}<br/>
                    </c:forEach>
                </td>
            </tr>
        </c:forEach>
    </table>
</c:if>
<c:if test="${empty suggestedList}">
    <div style="color:gray;">Chưa có gợi ý. Nhấn “Gợi ý lịch (Hill Climbing)” ở trên để tạo.</div>
</c:if>