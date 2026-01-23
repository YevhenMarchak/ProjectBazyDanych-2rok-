package dao;

import DataBase.DataBaseConnection;
import model.Suppliers;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class suppliersDao {


    public void addSupplier(Suppliers supplier) {
        String sql = "CALL suppliers_create(?, ?, ?, ?, ?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, supplier.getCompany_name());
            stmt.setString(2, supplier.getAddress());
            stmt.setString(3, supplier.getPhone());
            stmt.setString(4, supplier.getEmail());
            stmt.setString(5, supplier.getTax_id());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }



    public List<Suppliers> getAllSuppliers() {
        List<Suppliers> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM suppliers_read_all()";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                suppliers.add(new Suppliers(
                        rs.getInt("supplier_id"),
                        rs.getString("company_name"),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("tax_id")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return suppliers;
    }


    public Suppliers getSupplierById(int id) {
        String sql = "SELECT * FROM suppliers_read_one(?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Suppliers(
                            rs.getInt("supplier_id"),
                            rs.getString("company_name"),
                            rs.getString("address"),
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


    public void updateSupplier(Suppliers supplier) {
        String sql = "CALL suppliers_update(?, ?, ?, ?, ?, ?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, supplier.getSupplier_id());
            stmt.setString(2, supplier.getCompany_name());
            stmt.setString(3, supplier.getAddress());
            stmt.setString(4, supplier.getPhone());
            stmt.setString(5, supplier.getEmail());
            stmt.setString(6, supplier.getTax_id());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }



    public void deleteSupplier(int supplierId) {
        String sql = "CALL suppliers_delete(?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, supplierId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


}
