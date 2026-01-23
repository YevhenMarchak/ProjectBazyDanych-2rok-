package dao;

import DataBase.DataBaseConnection;
import model.categories;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class categoriesDao {

    public void addCategory(categories category) {
        String sql =
                "CALL categories_create(" +
                        "?::varchar, " +
                        "?::varchar" +
                        ")";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    public List<categories> getAllCategories() {
        List<categories> list = new ArrayList<>();
        String sql = "SELECT * FROM categories_read_all()";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new categories(
                        rs.getInt("category_id"),
                        rs.getString("name"),
                        rs.getString("description")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return list;
    }

    public categories getCategoryById(int id) {
        String sql = "SELECT * FROM categories_read_one(?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new categories(
                            rs.getInt("category_id"),
                            rs.getString("name"),
                            rs.getString("description")
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return null;
    }

    public void updateCategory(categories category) {
        String sql =
                "CALL categories_update(" +
                        "?::bigint, " +
                        "?::varchar, " +
                        "?::varchar" +
                        ")";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, category.getCategory_id());
            stmt.setString(2, category.getName());
            stmt.setString(3, category.getDescription());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    public void deleteCategory(int categoryId) {
        String sql = "CALL categories_delete(?::bigint)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
