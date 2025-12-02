package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Invoice;

public class InvoiceDao extends BaseDao implements IDao<Invoice> {

	@Override
	public void insert(Invoice invoice) throws SQLException {
		// TODO Auto-generated method stub
		String sql = "INSERT INTO Invoice (studentId, semester, academicYear, totalCredits, totalTuition, "
                + "paymentStatus, paymentDate, createdAt) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		try(Connection con=getConnection();
				PreparedStatement ps=con.prepareStatement(sql)){
			ps.setString(1, invoice.getStudentId());
			ps.setInt(2, invoice.getSemester());
			ps.setString(3, invoice.getAcademicYear());
			ps.setInt(4, invoice.getTotalCredits());
			ps.setDouble(5, invoice.getTotalTuition());
			ps.setString(6, invoice.getPaymentStatus());
			ps.setDate(7, invoice.getPaymentDate());
			ps.setDate(8, invoice.getCreatedAt());
			ps.executeUpdate();
		}
		
		
	}

	@Override
	public void update(Invoice invoice) throws SQLException {
		// TODO Auto-generated method stub
		String sql = "UPDATE Invoice SET studentId=?, semester=?, academicYear=?, totalCredits=?, "
                + "totalTuition=?, paymentStatus=?, paymentDate=?, createdAt=? WHERE invoiceId=?";
     try (Connection con = getConnection();
          PreparedStatement ps = con.prepareStatement(sql)) {
    	 ps.setString(1, invoice.getStudentId());
    	 ps.setInt(2, invoice.getSemester());
    	 ps.setString(3, invoice.getAcademicYear());
    	 ps.setInt(4, invoice.getTotalCredits());
    	 ps.setDouble(5, invoice.getTotalTuition());
    	 ps.setString(6, invoice.getPaymentStatus());
    	 ps.setDate(7, invoice.getPaymentDate());
    	 ps.setDate(8, invoice.getCreatedAt());
    	 ps.setInt(9, invoice.getInvoiceId());
    	 ps.executeUpdate();
     }
		
	}

	@Override
	public void delete(String id) throws SQLException {
		// TODO Auto-generated method stub
		 String sql = "DELETE FROM Invoice WHERE invoiceId=?";
	        try (Connection con = getConnection();
	             PreparedStatement ps = con.prepareStatement(sql)) {
	        	ps.setInt(1, Integer.parseInt(id));
	        	ps.executeUpdate();
	        }
		
	}

	@Override
	public Invoice getById(String id) throws SQLException {
		// TODO Auto-generated method stub
		 String sql = "SELECT * FROM Invoice WHERE invoiceId=?";
	        try (Connection con = getConnection();
	             PreparedStatement ps = con.prepareStatement(sql)) {
	        	ps.setInt(1, Integer.parseInt(id));
	            ResultSet rs = ps.executeQuery();
	            if (rs.next()) {
	                return mapRow(rs);
	            }
	        }
		return null;
	}

	@Override
	public List<Invoice> getAll() throws SQLException {
		// TODO Auto-generated method stub
		List<Invoice> list = new ArrayList<>();
        String sql = "SELECT * FROM Invoice";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
		return list;
	}
	private Invoice mapRow(ResultSet rs) throws SQLException {
        return new Invoice(
            rs.getInt("invoiceId"),
            rs.getString("studentId"),
            rs.getInt("semester"),
            rs.getString("academicYear"),
            rs.getInt("totalCredits"),
            rs.getDouble("totalTuition"),
            rs.getString("paymentStatus"),
            rs.getDate("paymentDate"),
            rs.getDate("createdAt")
        );
    }

}
