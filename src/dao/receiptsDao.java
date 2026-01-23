package dao;

import DataBase.DataBaseConnection;
import model.receipts;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class receiptsDao {

    public void addReceipt(receipts r) {
        String sql = "CALL receipts_create(?, ?, ?, ?, ?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, r.getSupplier_id());

            Integer empId = r.getEmployee_id();
            if (empId != null) {
                stmt.setInt(2, empId);
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            LocalDate d = r.getReceipt_date();
            if (d != null) {
                stmt.setDate(3, Date.valueOf(d));
            } else {
                stmt.setNull(3, Types.DATE);
            }

            stmt.setString(4, r.getExternal_invoice_no());
            stmt.setString(5, r.getStatus());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    public List<receipts> getAllReceipts() {
        List<receipts> list = new ArrayList<>();
        String sql = "SELECT * FROM receipts_read_all()";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Object empObj = rs.getObject("employee_id");
                Integer employeeId = (empObj != null) ? ((Number) empObj).intValue() : null;

                Date d = rs.getDate("receipt_date");
                LocalDate receiptDate = (d != null) ? d.toLocalDate() : null;

                list.add(new receipts(
                        rs.getInt("receipt_id"),
                        rs.getInt("supplier_id"),
                        employeeId,
                        receiptDate,
                        rs.getString("external_invoice_no"),
                        rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return list;
    }

    public receipts getReceiptById(int id) {
        String sql = "SELECT * FROM receipts_read_one(?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Object empObj = rs.getObject("employee_id");
                    Integer employeeId = (empObj != null) ? ((Number) empObj).intValue() : null;

                    Date d = rs.getDate("receipt_date");
                    LocalDate receiptDate = (d != null) ? d.toLocalDate() : null;

                    return new receipts(
                            rs.getInt("receipt_id"),
                            rs.getInt("supplier_id"),
                            employeeId,
                            receiptDate,
                            rs.getString("external_invoice_no"),
                            rs.getString("status")
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return null;
    }

    public void updateReceipt(receipts r) {
        String sql = "CALL receipts_update(?, ?, ?, ?, ?, ?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, r.getReceipt_id());
            stmt.setInt(2, r.getSupplier_id());

            Integer empId = r.getEmployee_id();
            if (empId != null) {
                stmt.setInt(3, empId);
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            LocalDate d = r.getReceipt_date();
            if (d != null) {
                stmt.setDate(4, Date.valueOf(d));
            } else {
                stmt.setNull(4, Types.DATE);
            }

            stmt.setString(5, r.getExternal_invoice_no());
            stmt.setString(6, r.getStatus());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    public void deleteReceipt(int receiptId) {
        String sql = "CALL receipts_delete(?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, receiptId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
