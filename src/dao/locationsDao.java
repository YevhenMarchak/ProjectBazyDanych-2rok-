package dao;

import DataBase.DataBaseConnection;
import model.locations;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class locationsDao {

    public void addLocation(locations location) {
        String sql = "CALL locations_create(?::varchar, ?::varchar, ?::numeric)";


        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, location.getLocation_code());
            stmt.setString(2, location.getLocation_type());
            stmt.setDouble(3, location.getMax_capacity());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    public List<locations> getAllLocations() {
        List<locations> list = new ArrayList<>();
        String sql = "SELECT * FROM locations_read_all()";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new locations(
                        rs.getInt("location_id"),
                        rs.getString("location_code"),
                        rs.getString("location_type"),
                        rs.getDouble("max_capacity")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return list;
    }

    public locations getLocationById(int id) {
        String sql = "SELECT * FROM locations_read_one(?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new locations(
                            rs.getInt("location_id"),
                            rs.getString("location_code"),
                            rs.getString("location_type"),
                            rs.getDouble("max_capacity")
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return null;
    }

    public void updateLocation(locations location) {
        String sql =
                "CALL locations_update(" +
                        "?::bigint, " +   // location_id
                        "?::text, " +     // location_code
                        "?::text, " +     // location_type
                        "?::numeric" +    // max_capacity
                        ")";


        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, location.getLocation_id());
            stmt.setString(2, location.getLocation_code());
            stmt.setString(3, location.getLocation_type());
            stmt.setDouble(4, location.getMax_capacity());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    public void deleteLocation(int locationId) {
        String sql = "CALL locations_delete(?)";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, locationId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
