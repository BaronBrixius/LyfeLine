import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class DBM {             //Database manager class for easier connecting and interacting
    static private final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    //  Database credentials
    static String DB_URL = "jdbc:mysql://localhost:3306?useTimezone=true&serverTimezone=UTC";
    static String USER = "root";
    static String PASS = "jan2306952431";
    static private Connection conn = null;
    static private String creationScript = "src/main/resources/Database Creation Script.sql";
    static private String schema;

    DBM() {             //Connect to server with default settings
        try {
            //Register JDBC driver
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected database successfully...");

            //Connect to schema
            useSchema("project");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    DBM(String schemaName) {
        try {
            //Register JDBC driver
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected database successfully...");

            //Connect to schema
            useSchema(schemaName);
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

            DBM.DB_URL = DB_URL;
            DBM.USER = user;
            DBM.PASS = pass;
        } catch (SQLException | ClassNotFoundException se) {
            se.printStackTrace();
        }
        System.out.println("Goodbye!");
    }

    DBM(String DB_URL, String user, String pass, String schemaName) {
        try {
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

    static boolean createDB() {                 //creates DB from default script
        try {
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
            return true;
        } catch (SQLException | FileNotFoundException se) {
            se.printStackTrace();
            return false;
        }
    }

    static boolean createDB(String newScript) {  //creates DB from alternate script
        String temp = DBM.creationScript;
        DBM.creationScript = newScript;
        if (createDB())
            return true;
        else {
            DBM.creationScript = temp;          //return to old creation script if new script fails
            return false;
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

    static <T> List<T> getFromDB(String query, CreatableFromDB<T> creatable) {
        //Runs query and uses Functional Interface method to parse each row into an object
        //note: functional interfaces can either use the implementation of an object, e.g. new User() makes a blank user to call the User class's implementation,
        //or can accept a lambda/method directly (must take in a ResultSet and output an Object, e.g. rs -> rs.getString("Name") will return String objects from the Name field)
        List<T> out = new ArrayList<>();

        try {
            //Runs input query to get ResultSet
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            //Add created object to list
            while (rs.next()) {
                out.add(creatable.createFromDB(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return out;
    }

    static ResultSet executeQuery(String query) throws SQLException {           //generic executeQuery command, just already hooked to DB for convenience
        ResultSet out = null;

        try {
            Statement stmt = conn.createStatement();
            out = stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return out;
    }

    static <T> boolean updateInDB(DBObject<T>... update) throws SQLException {      //updates the records of all inserted objects
        try {                                                                       //DON'T INSERT OBJECTS OF DIFFERENT TYPES
            Statement stmt = conn.createStatement();
            for (DBObject<T> t : update) {
                stmt.execute(t.getUpdateQuery());
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    static <T> boolean insertIntoDB(DBObject<T>... insert) throws SQLException, SQLIntegrityConstraintViolationException {
        try {                                                                           //inserts object(s) into DB as defined in each object's class
            Statement stmt = conn.createStatement();                                    //DON'T INSERT OBJECTS OF DIFFERENT TYPES
            ResultSet rs;
            for (DBObject<T> t : insert) {
                stmt.executeUpdate(t.getInsertQuery(), Statement.RETURN_GENERATED_KEYS);
                //after insertion, get the autogenerated ID and pass it to object that was inserted
                rs = stmt.getGeneratedKeys();
                if (rs != null && rs.next())
                    t.setID(rs.getInt(1));
            }
            return true;
        } catch (SQLIntegrityConstraintViolationException se) {
            throw se;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    static boolean useSchema(String schemaName) {                   //swaps to a different schema, creating it if it doesn't exist
        try {                                                       //note: you may want to rerun createDB() if on a brand new schema
            System.out.println("Connecting to schema...");
            Statement stmt = conn.createStatement();
            if (!stmt.executeQuery("SHOW DATABASES LIKE '" + schemaName + "';").next())
                stmt.execute("CREATE SCHEMA `" + schemaName + "`");
            stmt.execute("USE " + schemaName);
            DBM.schema = schemaName;
            System.out.println("Schema connected to successfully...");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    static boolean dropSchema() {                                   //drop current schema
        try {
            Statement stmt = conn.createStatement();
            stmt.execute("DROP DATABASE IF EXISTS " + schema);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    boolean close() {               //close the connection when you're done please
        try {
            if (conn != null)
                conn.close();
            return true;
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
    }
}
