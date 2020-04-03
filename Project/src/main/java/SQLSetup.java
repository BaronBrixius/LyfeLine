import java.sql.*;

public class SQLSetup {
    public static void main(String[] args) {
        try {
            System.out.println(Class.forName("com.mysql.cj.jdbc.Driver"));

            Connection conn;
            Statement statement;
            ResultSet resultSet;

            //Commented out since you can't test database connections without a database set up.
            /*conn = DriverManager.getConnection("jdbc:mysql://localhost/sys", "Max", "Gunkle007");
            statement = conn.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM sys.test WHERE id = 2");

            while (resultSet.next())
                System.out.println(resultSet.getString("user_name"));

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();*/
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
