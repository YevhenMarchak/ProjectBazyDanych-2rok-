package dao;

import DataBase.DataBaseConnection;
import model.EmployeeProductivitySummary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class employeeProductivityDao {

    public List<EmployeeProductivitySummary> getEmployeeProductivity() {

        List<EmployeeProductivitySummary> list = new ArrayList<>();

        String sql =
                "SELECT e.employee_id, e.first_name, e.last_name, " +
                        "       p.clients_count, p.productivity_level " +
                        "FROM employee_productivity() p " +
                        "JOIN employees e ON e.employee_id = p.employee_id " +
                        "ORDER BY p.clients_count DESC";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new EmployeeProductivitySummary(
                        rs.getLong("employee_id"),
                        rs.getString("first_name") + " " +
                                rs.getString("last_name"),
                        rs.getInt("clients_count"),
                        rs.getString("productivity_level")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return list;
    }
}
