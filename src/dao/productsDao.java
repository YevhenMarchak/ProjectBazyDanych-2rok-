package dao;

import DataBase.DataBaseConnection;
import model.products;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class productsDao {

    public void addProduct(products product) {
        String sql =
                "CALL products_create(" +
                        "?::text, " +          // sku
                        "?::text, " +          // name
                        "?::text, " +          // description
                        "?::bigint, " +        // category_id
                        "?::bigint, " +        // supplier_id
                        "?::numeric, " +       // weight
                        "?::text" +            // dimensions
                        ")";


        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getSku());
            stmt.setString(2, product.getName());
            stmt.setString(3, product.getDescription());
            stmt.setInt(4, product.getCategory_id());

            Integer supplierId = product.getSupplier_id();
            if (supplierId != null) {
                stmt.setInt(5, supplierId);
            } else {
                stmt.setNull(5, Types.BIGINT);
            }

            stmt.setDouble(6, product.getWeight());
            stmt.setString(7, product.getDimensions());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    public List<products> getAllProducts() {
        List<products> list = new ArrayList<>();
        String sql = "SELECT * FROM products_read_all()";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Object supObj = rs.getObject("supplier_id");
                Integer supplierId = (supObj != null) ? ((Number) supObj).intValue() : null;

                list.add(new products(
                        rs.getInt("product_id"),
                        rs.getString("sku"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("category_id"),
                        supplierId,
                        rs.getDouble("weight"),
                        rs.getString("dimensions")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return list;
    }

    public products getProductById(int id) {
        String sql = "SELECT * FROM products_read_one(?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Object supObj = rs.getObject("supplier_id");
                    Integer supplierId = (supObj != null) ? ((Number) supObj).intValue() : null;

                    return new products(
                            rs.getInt("product_id"),
                            rs.getString("sku"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getInt("category_id"),
                            supplierId,
                            rs.getDouble("weight"),
                            rs.getString("dimensions")
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return null;
    }

    public void updateProduct(products product) {
        String sql =
                "CALL products_update(" +
                        "?::bigint, " +     // product_id
                        "?::text, " +       // sku
                        "?::text, " +       // name
                        "?::text, " +       // description
                        "?::bigint, " +     // category_id
                        "?::bigint, " +     // supplier_id
                        "?::numeric, " +    // weight
                        "?::text" +         // dimensions
                        ")";


        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, product.getProduct_id());
            stmt.setString(2, product.getSku());
            stmt.setString(3, product.getName());
            stmt.setString(4, product.getDescription());
            stmt.setInt(5, product.getCategory_id());

            Integer supplierId = product.getSupplier_id();
            if (supplierId != null) {
                stmt.setInt(6, supplierId);
            } else {
                stmt.setNull(6, Types.BIGINT);
            }

            stmt.setDouble(7, product.getWeight());
            stmt.setString(8, product.getDimensions());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    public void deleteProduct(int productId) {
        String sql = "CALL products_delete(?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
