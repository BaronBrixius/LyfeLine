package database;

import database.DBM;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class DBMUnitTest {
    static final private String SCHEMA = "test";
    static private int testCount = 0;
    static private PreparedStatement stmt;
    static private ResultSet rs;

    @BeforeAll
    static void init() throws SQLException, IOException, ClassNotFoundException {
        new DBM(SCHEMA);
        DBM.setupSchema();
    }

    @AfterAll
    static void finish() throws SQLException {
        DBM.conn.createStatement().execute("DROP DATABASE IF EXISTS test");
        DBM.conn.close();
    }

    @BeforeEach
    void setUp() {
        testCount++;
        System.out.println("Test " + testCount);

        try {
            DBM.setupSchema();
        } catch (SQLException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    void createDB() {
    }

    @Test
    void testCreateDB() {
    }

    @Test
    void getFromDB() {
    }

    @Test
    void updateInDB() {
    }

    @Test
    void insertIntoDB() {
    }

    @Test
    void useSchema() {
    }

    @Test
    void dropSchema() {
    }

    @Test
    void close() throws SQLException, ClassNotFoundException {
        DBM.conn.close();

        assertThrows(SQLException.class, () -> DBM.conn.createStatement());

        new DBM(SCHEMA);
    }
}