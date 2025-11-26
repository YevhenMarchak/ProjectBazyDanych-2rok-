import DataBase.DataBaseConnection;
import java.sql.Connection;
import java.sql.SQLException;
public class Main {
    public static void main(String[] args) {


            try (Connection conn = DataBaseConnection.getConnection()) {
                if (conn != null) {
                    System.out.println("Połączenie działa!");
                }
            } catch (SQLException e) {
                System.out.println("Błąd połączenia ");
                e.printStackTrace();
            }



    }
}