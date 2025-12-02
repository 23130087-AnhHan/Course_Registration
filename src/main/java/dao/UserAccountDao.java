package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.UserAccount;

public class UserAccountDao extends BaseDao implements IDao<UserAccount> {

	@Override
	public void insert(UserAccount user) throws SQLException {
		// TODO Auto-generated method stub
		String sql= "INSERT INTO UserAccount (user_id,full_name,email,password_hash,role) VALUES (?,?,?,?,?)";
		try(Connection con = getConnection();
				PreparedStatement ps=con.prepareStatement(sql)) {
			ps.setString(1, user.getUserId());
			ps.setString(2, user.getFullName());
			ps.setString(3, user.getEmail());
			ps.setString(4, user.getPasswordHash());
			ps.setString(5, user.getRole());
			ps.executeUpdate();
			
		} 
	}

	@Override
	public void update(UserAccount user) throws SQLException {
		// TODO Auto-generated method stub
		String sql= "UPDATE UserAccount SET full_name=?,email=?,password_hash=?,role=? WHERE user_id=?";
		try(Connection con=getConnection();
				PreparedStatement ps=con.prepareStatement(sql)){
			ps.setString(1, user.getFullName());
			ps.setString(2, user.getEmail());
			ps.setString(3, user.getPasswordHash());
			ps.setString(4, user.getRole());
			ps.setString(5, user.getUserId());
			ps.executeUpdate();
			
		}
		
	}

	@Override
	public void delete(String id) throws SQLException {
		// TODO Auto-generated method stub
		String sql="DELETE FROM UserAccount WHERE user_id=?";
		try(Connection con= getConnection();
				PreparedStatement ps=con.prepareStatement(sql)){
			ps.setString(1, id);
			ps.executeUpdate();
			
		}
		
	}

	@Override
	public UserAccount getById(String id) throws SQLException {
		// TODO Auto-generated method stub
		String sql="SELECT * FROM UserAccount WHERE user_id=?";
		try(Connection con=getConnection(); 
				PreparedStatement ps=con.prepareStatement(sql)){
			ps.setString(1, id);
			try(ResultSet rs=ps.executeQuery()){
				if(rs.next()) {
					return new UserAccount(
							rs.getString("user_id"),
							rs.getString("full_name"),
							rs.getString("email"),
		                    rs.getString("password_hash"),
		                    rs.getString("role"),
		                    rs.getDate("created_at")
							);
				}
			}
		}
		return null;
	}

	@Override
	public List<UserAccount> getAll() throws SQLException {
		// TODO Auto-generated method stub
		List<UserAccount> list = new ArrayList<>();
		String sql= "SELECT * FROM UserAccount";
		try(Connection con= getConnection();
				PreparedStatement ps= con.prepareStatement(sql);
				ResultSet rs=ps.executeQuery()){
			while(rs.next()) {
				list.add(new UserAccount(
						rs.getString("user_id"),
	                    rs.getString("full_name"),
	                    rs.getString("email"),
	                    rs.getString("password_hash"),
	                    rs.getString("role"),
	                    rs.getDate("created_at")
						
						));
			}
		}
		return list;
		
	}

}
