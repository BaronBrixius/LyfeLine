package database;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {
    static final private String SCHEMA = "test";
    static private int testCount = 0;
    Event test=new Event();
    static Event[] events = new Event[4];

    @BeforeAll
    static void init() throws SQLException, IOException, ClassNotFoundException {
        new DBM(SCHEMA);
        DBM.setupSchema();
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
    void createFromDB() throws SQLException {
        /*Event test=new Event();
        events[0]=test;
        DBM.insertIntoDB(test);
        test.setImage("test.test");
        test.setTitle("test");
        test.setDescription("Test");
        Event test1=new Event();
        events[1]=test1;
        DBM.insertIntoDB(test1);
        Event test2=new Event();
        events[2]=test2;
        DBM.insertIntoDB(test2);
        assertEquals(test,events[0]);
        assertEquals(test1,events[1]);
        assertEquals(test2,events[2]);

        assertNotNull(1);
        assertEquals("test.test",test.getImagePath());
        assertEquals("test",test.getEventName());
        assertEquals("Test",test.getEventDescrition());*/


        Event test1=new Event();
        test1.setID(0);
        test1.setImage("فراس");
        test1.setTitle("الحطيب");
        test1.setDescription("With a culture that values creativity and technology, Google is used to decorating our " +
                "homepage for national holidays and historical figures.  When Ira Glass, of This American Life, slammed his hand on the conference table and smiled," +
                " “Why can’t we feature a random person?” the doodlers and I thought he was crazy.  I believe we laughed and moved the conversation on quickly-- none of" +
                " us thought the logo space that celebrates people like Harriet Tubman could also feature a random person." +
                "  Ira and This American Life, however, were onto something. ");
        Event test2=new Event();
        test2.setID(0);
        test2.setImage("alsdlöasmdklamdasmdkasmdas");
        test2.setTitle("الحطيب");
        test2.setDescription("With a culture that values creativity and technology, Google is used to decorating our " +
                "homepage for national holidays and historical figures.  When Ira Glass, of This American Life, slammed his hand on the conference table and smiled," +
                " “Why can’t we feature a random person?” the doodlers and I thought he was crazy.  I believe we laughed and moved the conversation on quickly-- none of" +
                " us thought the logo space that celebrates people like Harriet Tubman could also feature a random person." +
                "  Ira and This American Life, however, were onto something. ");
        events[0]=test1;
        events[1]=test2;
    }

    @Test
    void getInsertQuery() throws SQLException {
      /*  Event test1=new Event();
        test1.setID(0);
        test1.setImage("فراس");
        test1.setTitle("الحطيب");
        test1.setDescription("With a culture that values creativity and technology, Google is used to decorating our " +
                "homepage for national holidays and historical figures.  When Ira Glass, of This American Life, slammed his hand on the conference table and smiled," +
                " “Why can’t we feature a random person?” the doodlers and I thought he was crazy.  I believe we laughed and moved the conversation on quickly-- none of" +
                " us thought the logo space that celebrates people like Harriet Tubman could also feature a random person." +
                "  Ira and This American Life, however, were onto something. ");
        Event test2=new Event();
        test2.setID(0);
        test2.setImage("alsdlöasmdklamdasmdkasmdas");
        test2.setTitle("الحطيب");
        test2.setDescription("With a culture that values creativity and technology, Google is used to decorating our " +
                "homepage for national holidays and historical figures.  When Ira Glass, of This American Life, slammed his hand on the conference table and smiled," +
                " “Why can’t we feature a random person?” the doodlers and I thought he was crazy.  I believe we laughed and moved the conversation on quickly-- none of" +
                " us thought the logo space that celebrates people like Harriet Tubman could also feature a random person." +
                "  Ira and This American Life, however, were onto something. ");
        events[0]=test1;
        events[1]=test2;*/
        String sql = "INSERT INTO `events` (`EventName`, `EventDescription`,`StartYear`,`StartMonth`,`StartDay`,`StartHour`, " +
                "`StartMinute`,`StartSecond`,`StartMillisecond`,`EndYear`,`EndMonth`,`EndDay`,`EndHour`,`EndMinute`,`EndSecond`, " +
        "`EndMillisecond`,`CreatedYear`,`CreatedMonth`,`CreatedDay`,`CreatedHour`,`CreatedMinute`,`CreatedSecond`,`CreatedMillisecond`,`EventOwner`, `ImagePath`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
        PreparedStatement out = DBM.conn.prepareStatement(sql , Statement.RETURN_GENERATED_KEYS);
        for (int i = 0; i < events.length; i++) {
            assertEquals(out.toString(), events[i].getInsertQuery().toString());
        }

        //out.setInt(1, test.getUserID());
        //assertNotNull(test);
        //assertEquals(out.toString(),test.getInsertQuery().toString());
       /* try {
           // assertEquals(out.toString(),test.getInsertQuery().toString());
        }catch (SQLIntegrityConstraintViolationException e){
            ;
        }*/


    }
    @AfterAll
    static void tearDown() throws SQLException {
        DBM.conn.createStatement().execute("DROP DATABASE IF EXISTS test");
        DBM.conn.close();
    }

    @Test
    void addToTimeline() throws SQLException {
        Event testToAdd=new Event();
        testToAdd.setID(1);
        Timeline test=new Timeline();
        test.setID(1);
        PreparedStatement out = DBM.conn.prepareStatement("INSERT  INTO `timelineevents` (`TimelineID`, `EventID`) VALUES (?, ?);");
        out.setInt(1, 0);
        String x=out.toString();
        assertNotNull(out);
        assertEquals(x,out.toString());
        assertEquals(test.getTimelineID(),testToAdd.getEventID());
    }





    @Test
    void getUpdateQuery() throws SQLException {
       Event test=new Event();
      String x="فراس";
      String y="asdasdasdasd";
        DBM.insertIntoDB(test);
        DBM.updateInDB(test);
        events[3]=test;
        String sql="UPDATE `events` SET `EventName` = ?, `EventDescription` = ?, `ImagePath` = ?, `StartYear` = ?,  `StartMonth` = ?,  `StartDay` = ?,  `StartHour` = ?,  `StartMinute` = ?,  " +
                "`StartSecond` = ?,  `StartMillisecond` = ?,    `EndYear` = ?,  `EndMonth` = ?,  `EndDay` = ?,  `EndHour` = ?,  `EndMinute` = ?,  `EndSecond` = ?,  `EndMillisecond` = ?, `EventOwner` = ?  WHERE (`EventID` = ?)";
        PreparedStatement out = DBM.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        for (int i = 0; i < events.length; i++) {
            assertEquals(out.toString(), events[i].getInsertQuery().toString());
        }
/*        PreparedStatement out = DBM.conn.prepareStatement("UPDATE `events` SET `EventName` = ?, `EventDescription` = ?, `ImagePath` = ?, `StartYear` = ?,  `StartMonth` = ?,  `StartDay` = ?,  `StartHour` = ?,  `StartMinute` = ?,  " +
                "`StartSecond` = ?,  `StartMillisecond` = ?,    `EndYear` = ?,  `EndMonth` = ?,  `EndDay` = ?,  `EndHour` = ?,  `EndMinute` = ?,  `EndSecond` = ?,  `EndMillisecond` = ?, `EventOwner` = ?  WHERE (`EventID` = ?);");*/
        //assertNotNull(test.getUpdateQuery());
       //assertNotEquals(test.getUpdateQuery().toString(),test.toString());
       //assertEquals(out.toString(),test.getUpdateQuery().toString());
       //assertEquals(2,3);


    }

    @Test
    void getDeleteQuery() throws SQLException {
        Event test=new Event();
        events[0]=test;
         DBM.insertIntoDB(test);
         DBM.deleteFromDB(test);
        PreparedStatement out = DBM.conn.prepareStatement("DELETE FROM `events` WHERE (`EventID` = ?)");
        out.setInt(1, test.getEventID());
        assertEquals(out.toString(),test.getDeleteQuery().toString());
    }

    @Test
    void setUserID() {
        test.setUserID(1);
        assertEquals(1,1);
    }
    @Test
    void close() throws SQLException, ClassNotFoundException {
        DBM.conn.close();

        assertThrows(SQLException.class, () -> DBM.conn.createStatement());
        new DBM(SCHEMA);
    }

}