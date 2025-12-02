<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="sectionHeader">KẾT QUẢ DUYỆT CỦA CỐ VẤN HỌC TẬP</div>
<table style="width:40%;">
    <tr>
        <th>Trạng thái</th>
        <td><input type="text" value="${approvalStatus.status}" readonly style="width:98%;" /></td>
    </tr>
    <tr>
        <th>Lời nhắn cho sinh viên</th>
        <td><textarea rows="3" readonly style="width:98%;">${approvalStatus.advisorMessage}</textarea></td>
    </tr>
</table>