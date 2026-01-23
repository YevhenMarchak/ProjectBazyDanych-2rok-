package dao;

import DataBase.DataBaseConnection;
import model.employees;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class employeesDao {

    public void addEmployee(employees employee) {
        String sql =
                "CALL employees_create(" +
                        "?::varchar, " +
                        "?::varchar, " +
                        "?::varchar, " +
                        "?::date, " +
                        "?::varchar, " +
                        "?::varchar" +
                        ")";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, employee.getFirst_name());
            stmt.setString(2, employee.getLast_name());
            stmt.setString(3, employee.getPosition());

            LocalDate hireDate = employee.getHire_date();
            if (hireDate != null) {
                stmt.setDate(4, Date.valueOf(hireDate));
            } else {
                stmt.setNull(4, Types.DATE);
            }

            stmt.setString(5, employee.getPhone());
            stmt.setString(6, employee.getEmail());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    public List<employees> getAllEmployees() {
        List<employees> list = new ArrayList<>();
        String sql = "SELECT * FROM employees_read_all()";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Date d = rs.getDate("hire_date");
                LocalDate hireDate = (d != null) ? d.toLocalDate() : null;

                list.add(new employees(
                        rs.getInt("employee_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("position"),
                        hireDate,
                        rs.getString("phone"),
                        rs.getString("email")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return list;
    }

    public employees getEmployeeById(int id) {
        String sql = "SELECT * FROM employees_read_one(?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Date d = rs.getDate("hire_date");
                    LocalDate hireDate = (d != null) ? d.toLocalDate() : null;

                    return new employees(
                            rs.getInt("employee_id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("position"),
                            hireDate,
                            rs.getString("phone"),
                            rs.getString("email")
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return null;
    }

    public void updateEmployee(employees employee) {
        String sql =
                "CALL employees_update(" +
                        "?::bigint, " +
                        "?::varchar, " +
                        "?::varchar, " +
                        "?::varchar, " +
                        "?::date, " +
                        "?::varchar, " +
                        "?::varchar" +
                        ")";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employee.getEmployee_id());
            stmt.setString(2, employee.getFirst_name());
            stmt.setString(3, employee.getLast_name());
            stmt.setString(4, employee.getPosition());

            LocalDate hireDate = employee.getHire_date();
            if (hireDate != null) {
                stmt.setDate(5, Date.valueOf(hireDate));
            } else {
                stmt.setNull(5, Types.DATE);
            }

            stmt.setString(6, employee.getPhone());
            stmt.setString(7, employee.getEmail());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    public void deleteEmployee(int employeeId) {
        String sql = "CALL employees_delete(?::bigint)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employeeId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
