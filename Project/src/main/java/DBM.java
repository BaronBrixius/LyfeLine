import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class DBM {             //Database manager class for easier connecting and interacting
    static private final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static private Connection conn = null;

    DBM() {
        try {
            //Register JDBC driver
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection("jdbc:mysql://localhost/Project", "root", "AJnuHA^8VKHht=uB");
            System.out.println("Connected database successfully...");

            //Connect to schema
            Statement stmt = conn.createStatement();
            stmt.execute("USE project");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    DBM(String DB_URL, String user, String pass) {
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

    DBM(String DB_URL, String schemaName, String user, String pass) {
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

    static <T> List<T> getFromDB(String query, CreatableFromDB<T> creatable) {
        List<T> out = new ArrayList<>();

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                out.add(creatable.createFromDB(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return out;
    }

    static ResultSet runQuery(String query) throws SQLException {
        ResultSet out = null;

        try {
            Statement stmt = conn.createStatement();
            out = stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return out;
    }

    <T> boolean insertIntoDB(DBObject<T> insert) throws SQLException {
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
}
