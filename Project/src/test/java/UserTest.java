import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
	static private DBM sut;
	static User[] users = new User[4];
	static User[] user = new User[4];

	@BeforeAll
	static void init() throws SQLException, IOException, ClassNotFoundException {
		sut = new DBM("jdbc:mysql://localhost", "root", "Password123", "project");
		DBM.setupSchema();
		createTestDB(); // Adds some rows to the database tables and exports them to .xml, don't need to
						// run this often
	}

	static void createTestDB() throws SQLException {
		User user1 = new User("John", "john@gmail.com", "somethingCool#1");
		users[0] = user1;
		DBM.insertIntoDB(user1);
		User user2 = new User("John", "john2@gmail.com", "somethingCool#2");
		users[1] = user2;
		DBM.insertIntoDB(user2);
		User user3 = new User("John", "john3@gmail.com", "somethingCool#3");
		users[2] = user3;
		DBM.insertIntoDB(user3);
		User user4 = new User("John", "john4@gmail.com", "somethingCool#4");
		users[3] = user4;
		DBM.insertIntoDB(user4);

	}

	@AfterAll
	static void tearDown() throws SQLException {
		DBM.conn.createStatement().execute("DROP DATABASE IF EXISTS test");
		DBM.conn.close();
	}

	@Test
	void validateUnique() throws SQLException {
		User user1 = new User("John", "john@gmail.com", "somethingCool#1");
		User user2 = new User("John", "johnny@gmail.com", "somethingCool#1");

		// Test if email returns false - exists
		assertFalse(User.validateUnique(user1.getUserEmail()));
		// Test if email returns true - does not exist
		assertTrue(User.validateUnique(user2.getUserEmail()));
		// check exception if not right format - missing @
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			user2.setUserEmail("jonny.gmail.com");
		});
		String expectedMessage = "Invalid email format";
		String actualMessage = exception.getMessage();
		assertTrue(actualMessage.contains(expectedMessage)); // Checks both that exception is thrown and correct message
																// printed
		// check exception if not right format - missing .
		Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
			user2.setUserEmail("jonny@gmailcom");
		});
		String expectedMessage1 = "Invalid email format";
		String actualMessage1 = exception.getMessage();
		assertTrue(actualMessage1.contains(expectedMessage1)); // Checks both that exception is thrown and correct
																// message printed
	}

	@Test
	void setPassword() {
		// IllegalArgumentException check
		// See if encrpt. is right??
		User user = new User("John", "johnny@gmail.com", "Th3Mind'5EyE!");
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			user.setPassword("secret");
		});
		String actualMessage = exception.getMessage();
		String expectedMessage = ("Invalid password, must include at least: one digit, one lower case, one upper case, one special character, no white space and be at least 8 character long");

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void createFromDB() throws SQLException {
		// Create objects from the DB and see if they are 4(cause I inserted 4)
		ResultSet rs;
		PreparedStatement stmt = DBM.conn.prepareStatement("SELECT COUNT(*) FROM users");
		rs = stmt.executeQuery();
		rs.next();
		int actual = rs.getInt(1);
		assertEquals(users.length, actual);

		// See if the database objects are the same as the ones I pushed
		PreparedStatement stmt1 = DBM.conn.prepareStatement("SELECT * FROM users");
		List<User> userList = DBM.getFromDB(stmt1, new User());
		for (int i = 0; i < users.length; i++) {
			assertEquals(users[i].getUserEmail(), userList.get(i).getUserEmail());
			assertEquals(users[i].getUser(), userList.get(i).getUser());
		}
	}

	@Test
	void getInsertQuery() {

		// try right
	}

	@Test
	void getUpdateQuery() {
		// Try updateQuery and see then if createFromDB() fits the changes
	}

	@Test
	void testToString() {
	}

	@Test
	void isAdmin() throws SQLException{ // test to ensure that admin toggles are being sent to database correctly
		// set previous users as admin (4 users)
		users[0].setAdmin(true);
		users[1].setAdmin(true);
		users[2].setAdmin(true);
		users[3].setAdmin(true);
		DBM.updateInDB(users);
		// create new users and set them all as admin (now total 8 users)
		User user1 = new User("John", "john5@gmail.com", "somethingCool#5");
		user[0] = user1;
		DBM.insertIntoDB(user1);
		User user2 = new User("John", "john6@gmail.com", "somethingCool#6");
		user[1] = user2;
		DBM.insertIntoDB(user2);
		User user3 = new User("John", "john7@gmail.com", "somethingCool#7");
		user[2] = user3;
		DBM.insertIntoDB(user3);
		User user4 = new User("John", "john8@gmail.com", "somethingCool#8");
		user[3] = user4;
		DBM.insertIntoDB(user4);
		// generate a list of users from database
		PreparedStatement stmt1 = DBM.conn.prepareStatement("SELECT * FROM users");
		List<User> userList = DBM.getFromDB(stmt1, new User());
		//loop through each user checking if the list from the database matches what their admin status was set to
		for (int i = 0; i < users.length; i++) {
			assertEquals(users[i].getAdmin(), userList.get(i).getAdmin());
		}
		// remove admin status from the last 4 users
		user[0].setAdmin(false);
		user[1].setAdmin(false);
		user[2].setAdmin(false);
		user[3].setAdmin(false);
		DBM.updateInDB(user);

		PreparedStatement stmt2 = DBM.conn.prepareStatement("SELECT * FROM users");
		List<User> userList1 = DBM.getFromDB(stmt2, new User());
		//loop through each user checking if the list from the database matches what their admin status was set to
		for (int i = 0; i < users.length; i++) {
			assertEquals(users[i].getAdmin(), userList1.get(i).getAdmin());
		}
	}
}