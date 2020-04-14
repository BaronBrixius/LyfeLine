import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//Database manager class for easier connecting and interacting
class DBM {
    static private final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static String DB_URL = "jdbc:mysql://localhost";
    static String USER = "root";
    static String PASS = "AJnuHA^8VKHht=uB";
    static String SCHEMA = "project";
    static Connection conn = null;
    static String creationScript = "src/main/resources/Database Creation Script.sql";

    DBM() throws ClassNotFoundException, SQLException {                                                         //Connect to server with default settings
        this(SCHEMA);
    }

    DBM(String SCHEMA) throws ClassNotFoundException, SQLException {                                            //Connect to server with alternate schema
        this(DB_URL, USER, PASS, SCHEMA);
    }

    DBM(String DB_URL, String USER, String PASS) throws ClassNotFoundException, SQLException {                  //Connect to alternate server
        this(DB_URL, USER, PASS, SCHEMA);
    }

    DBM(String DB_URL, String USER, String PASS, String SCHEMA) throws ClassNotFoundException, SQLException {   //Connect to server with alternate settings
        DBM.DB_URL = DB_URL;
        DBM.USER = USER;
        DBM.PASS = PASS;
        DBM.SCHEMA = SCHEMA;

        //Register JDBC driver
        Class.forName(JDBC_DRIVER);

        //Open a connection
        System.out.println("Connecting to a selected database...");
        conn = DriverManager.getConnection(DB_URL, USER, PASS);

        //Connect to schema
        useSchema(SCHEMA);

        System.out.println("Connected to database successfully.");
    }

    static void useSchema(String SCHEMA) throws SQLException {                  //swaps to a different schema, creating it if it doesn't exist
        System.out.println("Connecting to schema...");                              //note: you may want to rerun createDB() if on a brand new schema
        Statement stmt = conn.createStatement();
        if (!stmt.executeQuery("SHOW DATABASES LIKE '" + SCHEMA + "';").next())
            stmt.execute("CREATE SCHEMA `" + SCHEMA + "`");
        stmt.execute("USE " + SCHEMA);
        DBM.SCHEMA = SCHEMA;
    }

    static void setupSchema() throws SQLException, FileNotFoundException {                  //creates schema from default script
        setupSchema(creationScript);
    }

    static void setupSchema(String newScript) throws FileNotFoundException, SQLException {  //creates schema from alternate script
        String oldScript = creationScript;
        creationScript = newScript;
        try {
            Statement stmt = conn.createStatement();

            System.out.println("Deleting and recreating schema...");
            dropSchema();
            useSchema(SCHEMA);

            //Read and run the database creation script
            System.out.println("Creating table(s) in given database...");
            String[] statements = readFile(creationScript);
            for (String s : statements) {
                stmt.execute(s);
            }
            System.out.println("Schema created successfully.");
        } catch (SQLException | FileNotFoundException e) {
            creationScript = oldScript;          //return to old creation script if new script fails
            throw e;
        }
    }

    static String[] readFile(String creationScript) throws FileNotFoundException {      //private read-in method for DB creation script
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

    static <T> void insertIntoDB(DBObject<T>... insert) throws SQLException {   //DON'T INSERT OBJECTS OF DIFFERENT TYPES
        PreparedStatement stmt;
        ResultSet rs;
        for (DBObject<T> t : insert) {
            if (t == null)
                continue;
            stmt = t.getInsertQuery();
            stmt.execute();
            //after insertion, get the autogenerated ID and pass it to object that was inserted
            rs = stmt.getGeneratedKeys();
            if (rs != null && rs.next())
                t.setID(rs.getInt(1));
        }
    }

    static <T> void updateInDB(DBObject<T>... update) throws SQLException {     //DON'T INSERT OBJECTS OF DIFFERENT TYPES
        for (DBObject<T> t : update) {
            if (t == null)
                continue;
            t.getUpdateQuery().execute();
        }
    }

    static <T> void deleteFromDB(DBObject<T>... delete) throws SQLException {   //DON'T INSERT OBJECTS OF DIFFERENT TYPES
        for (DBObject<T> t : delete) {
            if (t == null)
                continue;
            t.getDeleteQuery().execute();
        }
    }

    static void dropSchema() throws SQLException {                                   //drop current schema
        conn.createStatement().execute("DROP DATABASE IF EXISTS " + SCHEMA);
    }

    void close() throws SQLException {                                  //close the connection when you're done please
        if (conn != null)
            conn.close();
    }
}
