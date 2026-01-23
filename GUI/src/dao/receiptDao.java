package dao;

import DataBase.DataBaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class receiptDao {

    public double getReceiptCompletionPercentage(long receiptId) {

        String sql = "SELECT receipt_completion_percentage(?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, receiptId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return 0.0;
    }
}
