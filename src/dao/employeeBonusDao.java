package dao;

import DataBase.DataBaseConnection;
import model.EmployeeBonusSummary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class employeeBonusDao {

    public List<EmployeeBonusSummary> getEmployeeBonusSummary(int month, int year) {

        List<EmployeeBonusSummary> list = new ArrayList<>();

        String sql =
                "SELECT e.first_name, e.last_name, " +
                        "       c.shipments_count, c.bonus_percent " +
                        "FROM calculate_employee_bonus(?, ?) c " +
                        "JOIN employees e ON e.employee_id = c.employee_id";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, month);
            stmt.setInt(2, year);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    list.add(new EmployeeBonusSummary(
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getInt("shipments_count"),
                            rs.getInt("bonus_percent")
                    ));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return list;
    }
}
