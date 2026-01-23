package dao;

import DataBase.DataBaseConnection;
import model.MostShippedProduct;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class productStatsDao {

    public List<MostShippedProduct> getMostShippedProducts(double minQuantity) {

        List<MostShippedProduct> list = new ArrayList<>();

        String sql =
                "SELECT p.product_id, p.name, m.total_quantity " +
                        "FROM most_shipped_products(?::numeric) m " +
                        "JOIN products p ON p.product_id = m.product_id " +
                        "ORDER BY m.total_quantity DESC";


        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, minQuantity);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new MostShippedProduct(
                            rs.getLong("product_id"),
                            rs.getString("name"),
                            rs.getDouble("total_quantity")
                    ));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return list;
    }
}
