import java.sql.*;

class SQLServer {
    static private final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private Connection conn = null;
    private Statement stmt = null;

    SQLServer() {
        System.out.println("Connecting to a selected database...");
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/Project", "root", "AJnuHA^8VKHht=uB");
            System.out.println("Connected database successfully...");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    SQLServer(String DB_URL, String schemaName, String user, String pass) {
        try {
            //STEP 2: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //STEP 3: Open a connection
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DB_URL, user, pass);
            System.out.println("Connected database successfully...");

            //STEP 4: Connect to schema
            stmt = conn.createStatement();
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

    boolean useSchema(String schemaName) throws SQLException {
        return stmt.execute("USE " + schemaName);
    }

    ResultSet runQuery(String query) throws SQLException {
        return stmt.executeQuery(query);
    }
}
