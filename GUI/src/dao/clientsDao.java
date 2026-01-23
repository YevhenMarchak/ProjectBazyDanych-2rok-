package dao;

import DataBase.DataBaseConnection;
import model.clients;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class clientsDao {

    public void addClient(clients client) {
        String sql =
                "CALL clients_create(" +
                        "?::varchar, " +
                        "?::varchar, " +
                        "?::varchar, " +
                        "?::varchar, " +
                        "?::varchar" +
                        ")";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, client.getCompany_name());
            stmt.setString(2, client.getDelivery_address());
            stmt.setString(3, client.getPhone());
            stmt.setString(4, client.getEmail());
            stmt.setString(5, client.getTax_id());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    public List<clients> getAllClients() {
        List<clients> list = new ArrayList<>();
        String sql = "SELECT * FROM clients_read_all()";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new clients(
                        rs.getInt("client_id"),
                        rs.getString("company_name"),
                        rs.getString("delivery_address"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("tax_id")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return list;
    }

    public clients getClientById(int id) {
        String sql = "SELECT * FROM clients_read_one(?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new clients(
                            rs.getInt("client_id"),
                            rs.getString("company_name"),
                            rs.getString("delivery_address"),
                            rs.getString("phone"),
                            rs.getString("email"),
                            rs.getString("tax_id")
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return null;
    }

    public void updateClient(clients client) {
        String sql =
                "CALL clients_update(" +
                        "?::bigint, " +
                        "?::varchar, " +
                        "?::varchar, " +
                        "?::varchar, " +
                        "?::varchar, " +
                        "?::varchar" +
                        ")";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, client.getClient_id());
            stmt.setString(2, client.getCompany_name());
            stmt.setString(3, client.getDelivery_address());
            stmt.setString(4, client.getPhone());
            stmt.setString(5, client.getEmail());
            stmt.setString(6, client.getTax_id());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    public void deleteClient(int clientId) {
        String sql = "CALL clients_delete(?::bigint)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, clientId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
