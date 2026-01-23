package dao;

import DataBase.DataBaseConnection;
import model.shipment_details;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class shipment_detailsDao {

    public void addShipmentDetail(shipment_details detail) {
        String sql = "CALL shipment_details_create(?, ?, ?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, detail.getShipment_id());
            stmt.setLong(2, detail.getProduct_id());
            stmt.setBigDecimal(3,
                    BigDecimal.valueOf(detail.getQuantity_to_ship()));

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }




    public List<shipment_details> getAllShipmentDetails() {
        List<shipment_details> list = new ArrayList<>();
        String sql = "SELECT * FROM shipment_details_read_all()";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new shipment_details(
                        rs.getInt("shipment_id"),
                        rs.getInt("product_id"),
                        rs.getDouble("quantity_to_ship")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return list;
    }

    public shipment_details getShipmentDetail(int shipmentId, int productId) {
        String sql = "SELECT * FROM shipment_details_read_one(?, ?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, shipmentId);
            stmt.setInt(2, productId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new shipment_details(
                            rs.getInt("shipment_id"),
                            rs.getInt("product_id"),
                            rs.getDouble("quantity_to_ship")
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return null;
    }

    public void updateShipmentDetail(shipment_details detail) {
        String sql = "CALL shipment_details_update(?, ?, ?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, detail.getShipment_id());
            stmt.setLong(2, detail.getProduct_id());
            stmt.setBigDecimal(3,
                    BigDecimal.valueOf(detail.getQuantity_to_ship()));

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }




    public void deleteShipmentDetail(int shipmentId, int productId) {
        String sql = "CALL shipment_details_delete(?, ?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, shipmentId);
            stmt.setLong(2, productId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    public List<shipment_details> getShipmentDetailsByShipmentId(int shipmentId) {

        List<shipment_details> list = new ArrayList<>();
        String sql = "SELECT * FROM shipment_details_read_all()";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                if (rs.getInt("shipment_id") == shipmentId) {
                    list.add(new shipment_details(
                            rs.getInt("shipment_id"),
                            rs.getInt("product_id"),
                            rs.getDouble("quantity_to_ship")
                    ));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return list;
    }




}
