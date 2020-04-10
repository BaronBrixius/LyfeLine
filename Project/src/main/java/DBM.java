import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class DBM {             //Database manager class for easier connecting and interacting
    static private final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    //  Database credentials
    static String DB_URL = "jdbc:mysql://localhost";
    static String USER = "root";
    static String PASS = "AJnuHA^8VKHht=uB";
    static Connection conn = null;
    static private String creationScript = "src/main/resources/Database Creation Script.sql";
    static private String schema;

    DBM() throws ClassNotFoundException, SQLException {             //Connect to server with default settings
        //Register JDBC driver
        Class.forName(JDBC_DRIVER);
        System.out.println("Connecting to a selected database...");
        conn = DriverManager.getConnection(DB_URL, USER, PASS);
        System.out.println("Connected database successfully...");

        //Connect to schema
        useSchema("project");
    }

    DBM(String schemaName) throws ClassNotFoundException, SQLException {
        //Register JDBC driver
        Class.forName(JDBC_DRIVER);
        System.out.println("Connecting to a selected database...");
        conn = DriverManager.getConnection(DB_URL, USER, PASS);
        System.out.println("Connected database successfully...");

        //Connect to schema
        useSchema(schemaName);
    }

    DBM(String DB_URL, String user, String pass) throws ClassNotFoundException, SQLException {
        //Register JDBC driver
        Class.forName(JDBC_DRIVER);

        //Open a connection
        System.out.println("Connecting to a selected database...");
        conn = DriverManager.getConnection(DB_URL, user, pass);
        System.out.println("Connected database successfully...");

        DBM.DB_URL = DB_URL;
        DBM.USER = user;
        DBM.PASS = pass;
    }

    DBM(String DB_URL, String user, String pass, String schemaName) throws ClassNotFoundException, SQLException {
        //Register JDBC driver
        Class.forName(JDBC_DRIVER);

        //Open a connection
        System.out.println("Connecting to a selected database...");
        conn = DriverManager.getConnection(DB_URL, user, pass);
        System.out.println("Connected database successfully...");

        DBM.DB_URL = DB_URL;
        DBM.USER = user;
        DBM.PASS = pass;

        //Connect to schema
        useSchema(schemaName);
    }

    static void createDB() throws SQLException, FileNotFoundException {                 //creates DB from default script
        Statement stmt = conn.createStatement();

        System.out.println("Deleting and recreating schema...");
        DBM.dropSchema();
        DBM.useSchema(schema);

        //Read and run the database creation script
        System.out.println("Creating table(s) in given database...");
        String[] statements = readFile(creationScript);
        for (String s : statements) {
            stmt.execute(s);
        }
    }

    static void createDB(String newScript) throws FileNotFoundException, SQLException {  //creates DB from alternate script
        String temp = DBM.creationScript;
        DBM.creationScript = newScript;
        try {
            createDB();
        } catch (SQLException e) {
            DBM.creationScript = temp;          //return to old creation script if new script fails
            throw e;
        }
    }

    private static String[] readFile(String creationScript) throws FileNotFoundException {      //private read-in method for DB creation script
        StringBuilder temp = new StringBuilder();
        File sql = new File(creationScript);
        Scanner sqlScan = new Scanner(sql);

        while (sqlScan.hasNextLine())
            temp.append(sqlScan.nextLine()).append("\n");

        sqlScan.close();

        temp.delete(temp.lastIndexOf(";"), temp.length());   //cuts last ; off final statement so that ; can be used as delimiter without empty statements
        return temp.toString().split(";");
    }

    //Runs PreparedStatement and uses Functional Interface method to parse each row returned into an object
    //note: functional interfaces can either use the implementation of an object, e.g. new User() makes a blank user to call the User class's implementation,
    //or can accept a lambda/method directly (must take in a ResultSet and output an Object, e.g. rs -> rs.getString("Name") will return String objects from the Name field)
    static <T> List<T> getFromDB(PreparedStatement query, CreatableFromDB<T> creatable) throws SQLException {
        List<T> out = new ArrayList<>();

        //Runs input query to get ResultSet, then adds created objects to list
        ResultSet rs = query.executeQuery();
        while (rs.next()) {
            out.add(creatable.createFromDB(rs));
        }
        return out;
    }

    static <T> void updateInDB(DBObject<T>... update) throws SQLException {     //updates the records of all inserted objects
        for (DBObject<T> t : update) {                                          //DON'T INSERT OBJECTS OF DIFFERENT TYPES
            t.getUpdateQuery().execute();
        }
    }

    static <T> void insertIntoDB(DBObject<T>... insert) throws SQLException {   //inserts object(s) into DB as defined in each object's class
        PreparedStatement stmt;                                                 //DON'T INSERT OBJECTS OF DIFFERENT TYPES
        ResultSet rs;
        for (DBObject<T> t : insert) {
            stmt = t.getInsertQuery();
            stmt.execute();
            //after insertion, get the autogenerated ID and pass it to object that was inserted
            rs = stmt.getGeneratedKeys();
            if (rs != null && rs.next())
                t.setID(rs.getInt(1));
        }
    }

    static void useSchema(String schemaName) throws SQLException {                   //swaps to a different schema, creating it if it doesn't exist
        System.out.println("Connecting to schema...");          //note: you may want to rerun createDB() if on a brand new schema
        Statement stmt = conn.createStatement();
        if (!stmt.executeQuery("SHOW DATABASES LIKE '" + schemaName + "';").next())
            stmt.execute("CREATE SCHEMA `" + schemaName + "`");
        stmt.execute("USE " + schemaName);
        DBM.schema = schemaName;
        System.out.println("Schema connected to successfully...");
    }

    static void dropSchema() throws SQLException {                                   //drop current schema
        Statement stmt = conn.createStatement();
        stmt.execute("DROP DATABASE IF EXISTS " + schema);
    }

    void close() throws SQLException {                                  //close the connection when you're done please
        if (conn != null)
            conn.close();
    }
}
