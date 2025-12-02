package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCUtils {

    private static final String URL = "jdbc:mysql://localhost:3306/course_registration_system?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    // Lấy kết nối
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // optional với MySQL 8+
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Không tìm thấy driver MySQL", e);
        }

        // Trả về Connection, ném SQLException nếu lỗi
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Đóng kết nối an toàn
    public static void closeConnection(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                System.err.println("Lỗi khi đóng kết nối CSDL: " + e.getMessage());
            }
        }
    }
    
    // Kiểm tra kết nối (tùy chọn)
    public static void testConnection() {
        try (Connection con = getConnection()) {
            System.out.println("Kết nối CSDL thành công!");
        } catch (SQLException e) {
            System.err.println("Kết nối CSDL thất bại: " + e.getMessage());
        }
    }
}
