package main.java;

import java.sql.*;

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

public class SQLSetup {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/test_db";

    //  Database credentials
    static final String USER = "Halli";
    static final String PASS = "dragon";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        try{
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected database successfully...");

            //STEP 4: Execute a query
            System.out.println("Creating table in given database...");
            stmt = conn.createStatement();


            String sql = "CREATE TABLE REGISTRATION " +
                    "(id INTEGER not NULL, " +
                    " first VARCHAR(255), " +
                    " last VARCHAR(255), " +
                    " age INTEGER, " +
                    " PRIMARY KEY ( id ))";

            stmt.executeUpdate(sql);
            System.out.println("Created table in given database...");
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
}
