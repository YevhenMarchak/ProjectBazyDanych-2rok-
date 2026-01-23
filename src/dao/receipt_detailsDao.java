package dao;

import DataBase.DataBaseConnection;
import model.receipt_details;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class receipt_detailsDao {

    public void addReceiptDetail(receipt_details detail) {
        String sql = "CALL receipt_details_create(?, ?, ?, ?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, detail.getReceipt_id());
            stmt.setLong(2, detail.getProduct_id());
            stmt.setBigDecimal(3, detail.getExpected_quantity());
            stmt.setBigDecimal(4, detail.getReceived_quantity());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public receipt_details getReceiptDetail(long receiptId, long productId) {
        String sql = "SELECT * FROM receipt_details_read_one(?, ?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, receiptId);
            stmt.setLong(2, productId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new receipt_details(
                            rs.getLong("receipt_id"),
                            rs.getLong("product_id"),
                            rs.getBigDecimal("expected_quantity"),
                            rs.getBigDecimal("received_quantity")
                    );
                }
            }

        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                return null;
            }
            throw new RuntimeException(e.getMessage(), e);
        }

        return null;
    }

    public void updateReceiptDetail(receipt_details detail) {
        String sql = "CALL receipt_details_update(?, ?, ?, ?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, detail.getReceipt_id());
            stmt.setLong(2, detail.getProduct_id());
            stmt.setBigDecimal(3, detail.getExpected_quantity());
            stmt.setBigDecimal(4, detail.getReceived_quantity());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void deleteReceiptDetail(long receiptId, long productId) {
        String sql = "CALL receipt_details_delete(?, ?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, receiptId);
            stmt.setLong(2, productId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    public List<receipt_details> getAllReceiptDetails() {

        List<receipt_details> list = new ArrayList<>();

        String sql = "SELECT * FROM receipt_details_read_all()";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new receipt_details(
                        rs.getLong("receipt_id"),
                        rs.getLong("product_id"),
                        rs.getBigDecimal("expected_quantity"),
                        rs.getBigDecimal("received_quantity")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return list;
    }
    public List<receipt_details> getReceiptDetailsByReceiptId(long receiptId) {

        return getAllReceiptDetails()
                .stream()
                .filter(d -> d.getReceipt_id() == receiptId)
                .toList();
    }




}
