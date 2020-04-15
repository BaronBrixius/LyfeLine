import java.sql.*;
import java.util.List;

//Just example implement first and foremost to show the encryption/salting method to use.
//Of course all kinds of exception handling needed for inputs, such as email.
public class User implements Users {
	private int userID = 0;
	private String userName;
	private String userEmail;
	private String encryptedPass;
	private String salt;
	private boolean admin = false;

	public User() {
	} // dummy object for access to interface methods

	public User(String name, String email, String password) {
		setUserName(name);
		setUserEmail(email);
		setPassword(password);
	}

	public User(int userID, String name, String email, String encryptedPass, String salt, Boolean admin) {
		setID(userID);
		setUserName(name);
		setUserEmail(email);
		isAdmin(admin);
		this.encryptedPass = encryptedPass;
		this.salt = salt;
	}

	static boolean validateUnique(String email) throws SQLException {
		List<String> dbList = DBM.getFromDB(DBM.conn.prepareStatement("SELECT UserEmail FROM users"),
				rs -> rs.getString("UserEmail"));

		for (String db : dbList) {
			if (email.equalsIgnoreCase(db)) {
				return false;
			}
		}
		return true;
	}

	public String getUserEmail() {
		return this.userEmail;
	}

	@Override
	public void setUserEmail(String userEmail) throws IllegalArgumentException {
		if (!(userEmail.matches("\\p{all}+@[\\p{all}]+\\.\\p{all}+"))) // if not matches
																		// characters@characters.characters
			throw new IllegalArgumentException("Invalid email format");
		this.userEmail = userEmail;
	}

	@Override
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public boolean getAdmin() {
		return admin;
	}

	public String getUserName() {
		return this.userName;
	}

	public int getUserID() {
		return this.userID;
	}

	@Override
	public void isAdmin(Boolean admin) {
		this.admin = admin;
	}

	public boolean toggleAdmin() {
		admin = !admin;

		return admin;
	}

	@Override
	public void setPassword(String pass) {
		this.salt = PasswordEncryption.getSalt(30); // Length of the salt string
		this.encryptedPass = PasswordEncryption.generateSecurePassword(pass, this.salt);

	}

	@Override
	public String getUser(String email) { // Not sure about this one
		return null;
	}

	@Override
	public Boolean verifyPass(String pass, String encrypted, String salt) {
		return PasswordEncryption.verifyUserPassword(pass, encrypted, salt); // salt in DB associated with the encrypted
																				// password there and created with
																				// setPassword
	}

	@Override
	public User createFromDB(ResultSet rs) throws SQLException {
		int userID = rs.getInt("UserID");
		String name = rs.getString("UserName");
		String email = rs.getString("UserEmail");
		String encryptedPass = rs.getString("Password");
		String salt = rs.getString("Salt");
		boolean admin = rs.getBoolean("Admin");

		return new User(userID, name, email, encryptedPass, salt, admin);
	}

	@Override
	public PreparedStatement getInsertQuery() throws SQLException {
		if (userID > 0)
			throw new SQLIntegrityConstraintViolationException("User is already in DB.");

		PreparedStatement out = DBM.conn.prepareStatement(
				"INSERT INTO `users` (`UserName`, `UserEmail`, `Password`, `Salt`, `Admin`) VALUES (?,?,?,?,?)",
				Statement.RETURN_GENERATED_KEYS);
		out.setString(1, userName);
		out.setString(2, userEmail);
		out.setString(3, encryptedPass);
		out.setString(4, salt);
		out.setBoolean(5, admin);
		return out;
	}

	@Override
	public void setID(int id) {
		this.userID = id;
	}

	@Override
	public PreparedStatement getUpdateQuery() throws SQLException {
		PreparedStatement out = DBM.conn.prepareStatement(
				"UPDATE `users` SET `UserName` = ?, `UserEmail` = ?, `Password` = ?, `Salt` = ?, `Admin` = ? WHERE (`UserID` = ?)");
		out.setString(1, userName);
		out.setString(2, userEmail);
		out.setString(3, encryptedPass);
		out.setString(4, salt);
		out.setBoolean(5, admin);
		out.setInt(6, userID);
		return out;
	}

	@Override
	public String toString() {

		return "UserID: " + userID + " - Name: " + userName + " - Email: " + userEmail;

	}

}
