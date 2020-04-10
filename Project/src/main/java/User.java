import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

//Just example implement first and foremost to show the encryption/salting method to use.
//Of course all kinds of exception handling needed for inputs, such as email.
public class User implements Users {
    private int userID = 0;
    private String userEmail;
    private String userName;
    private String encryptedPass;
    private String salt;
    private boolean admin = false;

    public User() {
    }     //dummy object for access to interface methods


    public User(String email, String name, String password) {
        setUserEmail(email);
        setUserName(name);
        setPassword(password);
    }

    public User(int userID, String email, String name, String encryptedPass, String salt, Boolean admin) {
        setID(userID);
        setUserEmail(email);
        setUserName(name);
        isAdmin(admin);
        this.encryptedPass = encryptedPass;
        this.salt = salt;
    }

    static boolean validateUnique(String email) {
        List<String> dbList = DBM.getFromDB("SELECT UserEmail FROM users", rs -> rs.getString("UserEmail"));

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
        if (!(userEmail.matches("\\p{all}+@[\\p{all}]+\\.\\p{all}+")))      //if not matches characters@characters.characters
            throw new IllegalArgumentException("Invalid email format");
        this.userEmail = userEmail;
    }

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public void isAdmin(Boolean admin) {
        this.admin = admin;
    }

    @Override
    public void setPassword(String pass) {
        this.salt = PasswordUtils.getSalt(30); //Length of the salt string
        this.encryptedPass = PasswordUtils.generateSecurePassword(pass, this.salt);

    }

    @Override
    public String getUser(String email) { //Not sure about this one
        return null;
    }

    @Override
    public Boolean verifyPass(String pass, String encrypted, String salt) {
        String givenPassword = pass;
        String DBSecurePassword = encrypted; //the one from DB, created with setPassword
        String DBsalt = salt; //the one in DB associated with the encrypted password there and created with setPassword
        return PasswordUtils.verifyUserPassword(givenPassword, DBSecurePassword, DBsalt);
    }

    @Override
    public User createFromDB(ResultSet rs) throws SQLException {
        User out = null;
        try {
            int userID = rs.getInt("UserID");
            String name = rs.getString("UserName");
            String email = rs.getString("UserEmail");
            String encryptedPass = rs.getString("Password");
            String salt = rs.getString("Salt");
            boolean admin = rs.getBoolean("Admin");

            out = new User(userID, email, name, encryptedPass, salt, admin);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }

    @Override
    public String getInsertQuery() throws SQLException, SQLIntegrityConstraintViolationException {
        if (userID > 0)
            throw new SQLIntegrityConstraintViolationException("User is already in DB.");
        return "INSERT INTO `users` (`UserName`, `UserEmail`, `Password`, `Salt`, `Admin`) " +
                "VALUES ('" + userName + "', '" + userEmail + "', '" + encryptedPass + "', '" + salt + "', '" + (admin ? 1 : 0) + "');";

    }

    @Override
    public void setID(int id) {
        this.userID = id;
    }

    @Override
    public String getUpdateQuery() throws SQLException {
        return "UPDATE `users` SET `UserName` = '" + userName + "', `UserEmail` = '" + userEmail + "', `Password` = '" + encryptedPass + "', `Salt` = '" + salt + "', `Admin` = '" + (admin ? 1 : 0) + "'" +
                " WHERE (`UserID` = '" + userID + "')";
    }
    @Override
    public String toString() {
        return "UserID: " + userID + " Name: " + userName + " Email: " + userEmail + " Encrypted Password: " + encryptedPass  + " Salt: " + salt;
    }

}
