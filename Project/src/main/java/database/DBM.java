package database;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//Database manager class for easier connecting and interacting
public class DBM {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static Connection conn = null;
    public static String creationScript = "src/main/resources/Database_Creation_Script.sql";
    private static String DB_URL = "jdbc:mysql://localhost?useTimezone=true&serverTimezone=UTC";
    private static String USER = "root";
    private static String PASS = "AJnuHA^8VKHht=uB";
    private static String SCHEMA = "project";

    public DBM() throws ClassNotFoundException, SQLException {                                                         //Connect to server with default settings
        this(SCHEMA);
    }

    public DBM(String SCHEMA) throws ClassNotFoundException, SQLException {                                            //Connect to server with alternate schema
        this(DB_URL, USER, PASS, SCHEMA);
    }

    public DBM(String DB_URL, String USER, String PASS) throws ClassNotFoundException, SQLException {                  //Connect to alternate server
        this(DB_URL, USER, PASS, SCHEMA);
    }

    public DBM(String DB_URL, String USER, String PASS, String SCHEMA) throws ClassNotFoundException, SQLException {   //Connect to server with alternate settings
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

    public static void useSchema(String SCHEMA) throws SQLException {
        System.out.println("Connecting to schema...");
        try (Statement stmt = conn.createStatement()) {
            if (!stmt.executeQuery("SHOW DATABASES LIKE '" + SCHEMA + "';").next()) //swaps to a different schema, creating it if it doesn't exist
                stmt.execute("CREATE SCHEMA `" + SCHEMA + "`");                     //note: you may want to rerun setupSchema() if on a brand new schema
            stmt.execute("USE " + SCHEMA);
            DBM.SCHEMA = SCHEMA;
        }
    }

    public static void setupSchema() throws SQLException, FileNotFoundException {   //creates schema from default script
        setupSchema(creationScript);
    }

    public static void setupSchema(String newScript) throws FileNotFoundException, SQLException {  //creates schema from alternate script
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

    private static void runScript(String creationScript) throws FileNotFoundException, SQLException {      //private read-in method for DB creation script
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
    //How To Define The "creatable" Input:
    //functional interfaces can use the implementation defined by an object,    e.g. new User() returns a List<User>
    //or they can accept a lambda/method directly, which must take in a ResultSet and output an Object,     e.g. rs -> rs.getString("Name") will return a List<String> from the ResultSet's Name field
    public static <T> List<T> getFromDB(PreparedStatement query, CreatableFromDB<T> creatable) throws SQLException {
        List<T> out = new ArrayList<>();

        //Runs input query to get ResultSet, then adds created objects to list
        try (ResultSet rs = query.executeQuery()) {
            while (rs.next()) {
                out.add(creatable.createFromDB(rs));
            }
            return out;
        }
    }

    public static <T> void insertIntoDB(List<T> insert) throws SQLException {           //convenience method so inserting works with Lists
        insertIntoDB(asArray(insert));
    }

    public static <T> void insertIntoDB(DBObject<T>... insert) throws SQLException {    //DON'T INSERT OBJECTS OF DIFFERENT TYPES
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn.setAutoCommit(false);                              //turn off autocommit so statements don't run as soon as they're added to the batch
            for (DBObject<T> t : insert) {                          //iterate through all inserted T
                if (t == null)
                    continue;
                if (stmt == null)                                   //get T-specific prepared statement from the first nonnull T
                    stmt = t.getInsertQuery();

                if (t.getID() > 0)                                  //if something has an ID it already exists in DB
                    throw new SQLException("Cannot update " + t.getClass().getSimpleName() + " not in database.");

                t.setQueryValues(stmt);                             //get the values of each T and
                stmt.addBatch();                                    //add them to the statement in a batch
            }
            if (stmt == null)
                return;
            stmt.executeBatch();                                    //run the batch
            conn.commit();

            rs = stmt.getGeneratedKeys();
            for (DBObject<T> t : insert) {                          //after insertion, get the autogenerated ID and pass it to objects that were inserted
                if (t == null)
                    continue;
                rs.next();
                t.setID(rs.getInt(1));
            }
        } finally {
            if (stmt != null)
                stmt.close();
            if (rs != null)
                rs.close();
            conn.setAutoCommit(true);                               //turn autocommit back on so people can use the DB normally
        }
    }

    public static <T> void updateInDB(List<T> update) throws SQLException {             //convenience method so updating works with Lists
        updateInDB(asArray(update));
    }

    public static <T> void updateInDB(DBObject<T>... update) throws SQLException {      //DON'T INSERT OBJECTS OF DIFFERENT TYPES
        PreparedStatement stmt = null;
        try {
            conn.setAutoCommit(false);                              //turn off autocommit so statements don't run as soon as they're added to the batch
            for (DBObject<T> t : update) {                          //iterate through all inserted T
                if (t == null)
                    continue;
                if (stmt == null)                                   //get T-specific prepared statement from the first nonnull T
                    stmt = t.getUpdateQuery();

                if (t.getID() == 0)                                 //if something has no ID it doesn't exist in DB
                    throw new SQLException(t.getClass().getSimpleName() + " is already in database");

                t.setQueryValues(stmt);                             //get the values of each T and
                stmt.addBatch();                                    //add them to the statement in a batch
            }
            if (stmt != null)
                stmt.executeBatch();                                //run the batch
        } finally {
            if (stmt != null)
                stmt.close();
            conn.setAutoCommit(true);                               //turn autocommit back on so people can use the DB normally
        }
    }

    public static <T> void deleteFromDB(List<T> delete) throws SQLException {           //convenience method so deleting works with Lists
        deleteFromDB(asArray(delete));
    }

    public static <T> void deleteFromDB(DBObject<T>... delete) throws SQLException {           //DON'T INSERT OBJECTS OF DIFFERENT TYPES
        PreparedStatement stmt = null;
        try {
            conn.setAutoCommit(false);                              //turn off autocommit so statements don't run as soon as they're added to the batch
            for (DBObject<T> t : delete) {                          //iterate through all inserted T
                if (t == null)
                    continue;
                if (stmt == null)                                   //get T-specific prepared statement from the first nonnull T
                    stmt = t.getDeleteQuery();

                stmt.setInt(1, t.getID());              //get the ID of each T and
                stmt.addBatch();                                    //add them to the statement in a batch
            }

            if (stmt != null)
                stmt.executeBatch();                                //run the batch
        } finally {
            if (stmt != null)
                stmt.close();
            conn.setAutoCommit(true);                               //turn autocommit back on so people can use the DB normally
        }
    }

    public static <T> DBObject<T>[] asArray(List<T> list) {         //converts List to Array manually since java doesn't like generic arrays
        try {
            DBObject<T>[] asArray = (DBObject<T>[]) java.lang.reflect.Array.newInstance(list.get(0).getClass(), list.size());
            for (int i = 0; i < list.size(); i++) {
                asArray[i] = (DBObject<T>) list.get(i);
            }
            return asArray;
        } catch (ClassCastException e) {
            throw new ClassCastException("Class does not implement DBObject<T>");       //clearer exception message
        }
    }

    public static void dropSchema() throws SQLException {                                      //drop current schema
        conn.createStatement().execute("DROP DATABASE IF EXISTS " + SCHEMA);
    }

    public static void close() throws SQLException {                                           //close the connection when you're done please
        if (conn != null) {
            conn.close();
            conn = null;
        }
    }
}
