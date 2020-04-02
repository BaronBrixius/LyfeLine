import java.sql.*;

public class Main {
    public static void main(String[] args) {
        /*try {
            //Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/sys", "Max", "testPass");
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM sys.test WHERE id = 2");

            while (resultSet.next())
                System.out.println(resultSet.getString("user_name"));

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }*/

        System.out.print("Hello Purple!");
        System.out.print("My name is Haraldur Blöndal Kristjánsson");
    }
}