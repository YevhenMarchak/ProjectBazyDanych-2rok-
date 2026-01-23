package dao;

import DataBase.DataBaseConnection;
import model.shipments;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class shipmentsDao {

    public void addShipment(shipments s) {
        String sql = "CALL shipments_create(?, ?, ?, ?, ?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, s.getClient_id());

            if (s.getEmployee_id() != null) {
                stmt.setInt(2, s.getEmployee_id());
            } else {
                stmt.setNull(2, Types.BIGINT);
            }

            if (s.getShipment_date() != null) {
                stmt.setDate(3, Date.valueOf(s.getShipment_date()));
            } else {
                stmt.setNull(3, Types.DATE);
            }

            stmt.setString(4, s.getClient_order_no());
            stmt.setString(5, s.getStatus());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    public List<shipments> getAllShipments() {
        List<shipments> list = new ArrayList<>();
        String sql = "SELECT * FROM shipments_read_all()";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Object empObj = rs.getObject("employee_id");
                Integer employeeId = (empObj != null) ? ((Number) empObj).intValue() : null;

                Date d = rs.getDate("shipment_date");
                LocalDate shipDate = (d != null) ? d.toLocalDate() : null;

                list.add(new shipments(
                        rs.getInt("shipment_id"),
                        rs.getInt("client_id"),
                        employeeId,
                        shipDate,
                        rs.getString("client_order_no"),
                        rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return list;
    }

    public shipments getShipmentById(int id) {
        String sql = "SELECT * FROM shipments_read_one(?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Object empObj = rs.getObject("employee_id");
                    Integer employeeId = (empObj != null) ? ((Number) empObj).intValue() : null;

                    Date d = rs.getDate("shipment_date");
                    LocalDate shipDate = (d != null) ? d.toLocalDate() : null;

                    return new shipments(
                            rs.getInt("shipment_id"),
                            rs.getInt("client_id"),
                            employeeId,
                            shipDate,
                            rs.getString("client_order_no"),
                            rs.getString("status")
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return null;
    }

    public void updateShipment(shipments s) {
        String sql = "CALL shipments_update(?, ?, ?, ?, ?, ?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, s.getShipment_id());
            stmt.setInt(2, s.getClient_id());

            if (s.getEmployee_id() != null) {
                stmt.setInt(3, s.getEmployee_id());
            } else {
                stmt.setNull(3, Types.BIGINT);
            }

            if (s.getShipment_date() != null) {
                stmt.setDate(4, Date.valueOf(s.getShipment_date()));
            } else {
                stmt.setNull(4, Types.DATE);
            }

            stmt.setString(5, s.getClient_order_no());
            stmt.setString(6, s.getStatus());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    public void deleteShipment(int shipmentId) {
        String sql = "CALL shipments_delete(?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, shipmentId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}