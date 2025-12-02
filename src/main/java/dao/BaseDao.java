package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import util.JDBCUtils;

public abstract class BaseDao {
	protected Connection getConnection() throws SQLException {
		return JDBCUtils.getConnection();
	}
	
	protected void closeResources(Connection con, PreparedStatement ps, ResultSet rs) {
		try {
			if(rs!=null) rs.close();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		try {
			if(ps!=null) ps.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		try {
			if(con!=null) con.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
