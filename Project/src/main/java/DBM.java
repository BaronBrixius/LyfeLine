import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//Database manager class for easier connecting and interacting
class DBM {
    static private final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static String DB_URL = "jdbc:mysql://localhost?useTimezone=true&serverTimezone=UTC";
    static String USER = "root";
    static String PASS = "toor";
    static String SCHEMA = "project";
    static Connection conn = null;
    static String creationScript = "src/main/resources/Database_Creation_Script.sql";

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
        close();                        //if connection is already open, close it before making a new one

        DBM.DB_URL = DB_URL;
        DBM.USER = USER;
        DBM.PASS = PASS;
        DBM.SCHEMA = SCHEMA;

        //Register JDBC driver
        Class.forName(JDBC_DRIVER);

        //Open a connection
        System.out.println("Connecting to selected database...");
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
            System.out.println("Deleting and recreating schema...");
            dropSchema();
            useSchema(SCHEMA);

            //Read and run the database creation script
            System.out.println("Creating table(s) in given database...");
            runScript(creationScript);
            System.out.println("Schema created successfully.");
        } catch (SQLException | FileNotFoundException e) {
            creationScript = oldScript;          //return to old creation script if new script fails
            throw e;
        }
    }

    static void runScript(String creationScript) throws FileNotFoundException, SQLException {      //private read-in method for DB creation script
        File sql = new File(creationScript);
        Statement stmt = conn.createStatement();
        Scanner sqlScan = new Scanner(sql);
        sqlScan.useDelimiter(";[\\r\\n]{3,}");
        String query;

        while (sqlScan.hasNext()) {
            query = sqlScan.next();
            if (stmt != null && !query.isEmpty())
                stmt.execute(query);
        }

        sqlScan.close();
        if (stmt != null)
            stmt.close();
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

    static <T> void insertIntoDB(List<T> insert) throws SQLException {            //converts to array so it works with varargs
        insertIntoDB(asArray(insert));
    }

    static <T> void updateInDB(DBObject<T>... update) throws SQLException {     //DON'T INSERT OBJECTS OF DIFFERENT TYPES
        for (DBObject<T> t : update) {
            if (t == null)
                continue;
            t.getUpdateQuery().execute();
        }
    }

    static <T> void updateInDB(List<T> update) throws SQLException {            //converts to array so it works with varargs
        updateInDB(asArray(update));
    }

    static <T> void deleteFromDB(DBObject<T>... delete) throws SQLException {           //DON'T INSERT OBJECTS OF DIFFERENT TYPES
        for (DBObject<T> t : delete) {
            if (t == null)
                continue;
            t.getDeleteQuery().execute();
        }
    }

    static <T> void deleteFromDB(List<T> delete) throws SQLException {                  //converts to array so it works with varargs
        deleteFromDB(asArray(delete));
    }

    static <T> DBObject<T>[] asArray(List<T> list) {                                    //converts generic List to Array since normal methods hate casting like that
        try {
            DBObject<T>[] asArray = (DBObject<T>[]) java.lang.reflect.Array.newInstance(list.get(0).getClass(), list.size());
            for (int i = 0; i < list.size(); i++) {
                asArray[i] = (DBObject<T>) list.get(i);
            }
            return asArray;
        } catch (ClassCastException e) {
            throw new ClassCastException("Class does not implement DBObject<T>");       //clearer exception message for this project
        }
    }

    static void dropSchema() throws SQLException {                                      //drop current schema
        conn.createStatement().execute("DROP DATABASE IF EXISTS " + SCHEMA);
    }

    static void close() throws SQLException {                                                  //close the connection when you're done please
        if (conn != null) {
            conn.close();
            conn = null;
        }
    }
}
