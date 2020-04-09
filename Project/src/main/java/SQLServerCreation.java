import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

class SQLServerCreation {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/Project";
    static final String creationScript = "src/main/resources/Database Creation Script.sql";
    static final String schemaName = "project";
    //  Database credentials
    static final String USER = "root";
    static final String PASS = "AJnuHA^8VKHht=uB";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt;

        try {
            //STEP 2: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //STEP 3: Open a connection
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected database successfully...");

            //STEP 4: Execute the queries
            stmt = conn.createStatement();

            System.out.println("Deleting and recreating schema...");
            stmt.execute("DROP DATABASE IF EXISTS " + schemaName);
            stmt.execute("CREATE SCHEMA " + schemaName);
            stmt.execute("USE " + schemaName);

            System.out.println("Creating table(s) in given database...");
            String[] statements = readFile(creationScript);
            for (String s : statements) {
                stmt.execute(s);
            }

        } catch (SQLException | FileNotFoundException | ClassNotFoundException se) {
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

    private static String[] readFile(String creationScript) throws FileNotFoundException {
        StringBuilder temp = new StringBuilder();
        File sql = new File(creationScript);
        Scanner sqlScan = new Scanner(sql);

        while (sqlScan.hasNextLine())
            temp.append(sqlScan.nextLine()).append("\n");

        sqlScan.close();

        temp.delete(temp.lastIndexOf(";"), temp.length());   //cuts last ; off final statement so that ; can be used as delimiter without empty statements
        return temp.toString().split(";");
    }
}
