import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class DBManager {
    static private final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static private Connection conn = null;

    DBManager() {
        System.out.println("Connecting to a selected database...");
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/Project", "root", "AJnuHA^8VKHht=uB");
            System.out.println("Connected database successfully...");

            //Connect to schema

            Statement stmt = conn.createStatement();
            stmt.execute("USE project");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    DBManager(String DB_URL, String user, String pass) {
        try {
            //Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //Open a connection
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DB_URL, user, pass);
            System.out.println("Connected database successfully...");
        } catch (SQLException | ClassNotFoundException se) {
            se.printStackTrace();
        }
        System.out.println("Goodbye!");
    }

    DBManager(String DB_URL, String schemaName, String user, String pass) {
        try {
            //Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //Open a connection
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DB_URL, user, pass);
            System.out.println("Connected database successfully...");

            //Connect to schema
            Statement stmt = conn.createStatement();
            stmt.execute("USE " + schemaName);

        } catch (SQLException | ClassNotFoundException se) {
            se.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        System.out.println("Goodbye!");
    }

    public static <T> List<T> getObjectsFromDB(String query, DBObject<T> dbObject) {
        List<T> outList = new ArrayList<>();

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                outList.add(dbObject.createFromDB(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return outList;
    }

    public <T> boolean insertIntoDB(DBObject<T> insert) throws SQLException {
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(insert.getInsertQuery());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    boolean close() {
        try {
            if (conn != null)
                conn.close();
            return true;
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
    }

    boolean useSchema(String schemaName) throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.execute("USE " + schemaName);
    }

    ResultSet runQuery(String query) throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(query);
    }

}
