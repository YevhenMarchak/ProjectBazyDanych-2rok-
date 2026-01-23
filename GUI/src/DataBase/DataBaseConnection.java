package DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {

    private static final String URL =
            "jdbc:postgresql://localhost:5432/SystemZarzadzaniaMagazynem"
                    + "?currentSchema="
                    + "pkg_categories,"
                    + "pkg_clients,"
                    + "pkg_employees,"
                    + "pkg_suppliers,"
                    + "pkg_products,"
                    + "pkg_locations,"
                    + "pkg_inventory,"
                    + "pkg_receipts,"
                    + "pkg_shipments,"
                    + "analytics,"
                    + "public";

    private static final String USER = "postgres";
    private static final String PASSWORD = "admin123";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
