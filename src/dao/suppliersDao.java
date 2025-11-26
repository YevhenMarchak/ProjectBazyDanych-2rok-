package dao;
import model.Suppliers;
import DataBase.DataBaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class suppliersDao {
    public void addSupplier(Suppliers supplier) {
        String sql = "INSERT INTO suppliers(supplier_id, company_name, adress, phone, email, tax_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, supplier.getSupplier_id());
            stmt.setString(2, supplier.getCompany_name());
            stmt.setString(3, supplier.getAddress());
            stmt.setInt(4, supplier.getPhone());
            stmt.setString(5, supplier.getEmail());
            stmt.setInt(6, supplier.getTax_id());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }//koniec add

    public List<Suppliers> getAllSuppliers() {
        List<Suppliers> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM suppliers";

        try (Connection conn = DataBaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Suppliers supplier = new Suppliers(
                        rs.getInt("supplier_id"),
                        rs.getString("company_name"),
                        rs.getString("adress"),
                        rs.getInt("phone"),
                        rs.getString("email"),
                        rs.getInt("tax_id")

                );
                suppliers.add(supplier);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return suppliers;
    }//koniec get

    public void updateSupplier(Suppliers suppliers) {
        String sql = "UPDATE suppliers SET supplier_id=?, company_name=?, adress=?, phone=?, email=?, tax_id=?";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, suppliers.getSupplier_id());
            stmt.setString(2, suppliers.getCompany_name());
            stmt.setString(3, suppliers.getAddress());
            stmt.setInt(4, suppliers.getPhone());
            stmt.setString(5, suppliers.getEmail());
            stmt.setInt(6, suppliers.getTax_id());


            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteSupplier(Suppliers suppliers) {
        String sql = "DELETE FROM doctors WHERE supplier_id=?";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, suppliers.getSupplier_id());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}//koniec class





