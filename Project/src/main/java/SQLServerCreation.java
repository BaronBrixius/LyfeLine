import javax.xml.transform.Result;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Arrays;
import java.util.Scanner;

//Changed this little bit with some example of setting up connection and create simple table in the database
//Only thing you need to do to try it on your machine is to :
//Connect to your server in terminal/CMD and create the database test_DB :
//mysql -u root -p
//Enter password:
//CREATE DATABASE test_db;
//CREATE USER 'Halli' IDENTIFIED BY 'dragon'; YOU CAN USER YOUR OWN IF U WANT; THEN JUST ADD IT BELOW IN THE JAVA FIELD USER AND PASS
// USE test_db
//GRANT ALL ON test_db.* TO 'Halli'@'localhost'; OR YOUR USERNAME
//quit

//AFTER DOING ABOVE YOU CAN RUN THIS CODE ADN CREATE THE TABLE IN THE test_db.

public class SQLServerCreation {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/Project";
    static final String creationScript = "src/main/resources/Database Creation Script.sql";
    static final String schemaName = "project";
    static Connection conn = null;
    static Statement stmt = null;

    //  Database credentials
    static final String USER = "root";
    static final String PASS = "AJnuHA^8VKHht=uB";

    public static void main(String[] args) {
        try{
            //STEP 2: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //STEP 3: Open a connection
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected database successfully...");

            //STEP 4: Execute the queries
            Statement stmt = conn.createStatement();
            String[] statements = readFile(creationScript);

            System.out.println("Deleting and recreating schema...");
            stmt.execute("DROP DATABASE IF EXISTS " + schemaName);
            stmt.execute("CREATE SCHEMA " + schemaName);
            stmt.execute("USE " + schemaName);

            System.out.println("Creating table(s) in given database...");
            for (String s: statements) {
                stmt.execute(s);
            }

            //int[] results = stmt.executeBatch();

            //System.out.println(Arrays.toString(results));

        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e){
            //Handle errors for Class.forName.
            e.printStackTrace();
        }finally{
            //finally block used to close resources
            try{
                if(stmt!=null)
                    conn.close();
            }catch(SQLException se){
            }// do nothing
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }//end finally try
        }//end try
        System.out.println("Goodbye!");
    }

    private static String[] readFile(String creationScript) throws FileNotFoundException {
        StringBuilder temp = new StringBuilder();
        File sql = new File(creationScript);
        Scanner sqlScan = new Scanner(sql);

        while (sqlScan.hasNextLine())
            temp.append(sqlScan.nextLine()).append("\n");

        sqlScan.close();

        temp.delete(temp.lastIndexOf(";"),temp.length());   //cuts last ; off final statement so that ; can be used as delimiter without empty statements
        return temp.toString().split(";");

    }
}
