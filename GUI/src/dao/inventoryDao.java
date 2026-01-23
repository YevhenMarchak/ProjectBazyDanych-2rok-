package dao;

import DataBase.DataBaseConnection;
import model.inventory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class inventoryDao {

    public void addInventory(inventory inv) {
        String sql =
                "CALL inventory_create(" +
                        "?::bigint, " +   // product_id
                        "?::bigint, " +   // location_id
                        "?::numeric" +    // quantity
                        ")";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, inv.getProduct_id());
            stmt.setInt(2, inv.getLocation_id());
            stmt.setDouble(3, inv.getQuantity());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    public List<inventory> getAllInventory() {
        List<inventory> list = new ArrayList<>();
        String sql = "SELECT * FROM inventory_read_all()";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("last_updated");
                LocalDateTime lastUpdated = (ts != null) ? ts.toLocalDateTime() : null;

                list.add(new inventory(
                        rs.getInt("inventory_id"),
                        rs.getInt("product_id"),
                        rs.getInt("location_id"),
                        rs.getDouble("quantity"),
                        lastUpdated
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return list;
    }

    public inventory getInventoryById(int id) {
        String sql = "SELECT * FROM inventory_read_one(?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Timestamp ts = rs.getTimestamp("last_updated");
                    LocalDateTime lastUpdated = (ts != null) ? ts.toLocalDateTime() : null;

                    return new inventory(
                            rs.getInt("inventory_id"),
                            rs.getInt("product_id"),
                            rs.getInt("location_id"),
                            rs.getDouble("quantity"),
                            lastUpdated
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return null;
    }

    public void updateInventory(inventory inv) {
        String sql =
                "CALL inventory_update(" +
                        "?::bigint, " +   // inventory_id
                        "?::bigint, " +   // product_id
                        "?::bigint, " +   // location_id
                        "?::numeric" +    // quantity
                        ")";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, inv.getInventory_id());
            stmt.setInt(2, inv.getProduct_id());
            stmt.setInt(3, inv.getLocation_id());
            stmt.setDouble(4, inv.getQuantity());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    public void deleteInventory(int inventoryId) {
        String sql = "CALL inventory_delete(?::bigint)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, inventoryId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
