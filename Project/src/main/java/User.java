import java.sql.ResultSet;
import java.sql.SQLException;

//Just example implement first and foremost to show the encryption/salting method to use.
//Of course all kinds of exception handling needed for inputs, such as email.
public class User implements Users {
    private String email;
    private String name;
    private String encryptedPass;
    private String salt;
    private boolean admin = false;

    public User() { }     //dummy object for access to interface methods


    public User(String email, String name, String password) {
        setEmail(email);
        setName(name);
        setPassword(password);
    }

    public User(String email, String name, String password, Boolean admin) {
        setEmail(email);
        setName(name);
        isAdmin(admin);
        setPassword(password);
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public void setName(String name) {
        this.name = name;
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
            String name = rs.getString("UserName");
            String email = rs.getString("UserEmail");
            String encryptedPass = rs.getString("Password");
            boolean admin = rs.getBoolean("Admin");

            out = new User(email, name, encryptedPass, admin);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }

    @Override
    public String getInsertQuery() throws SQLException {
        return "INSERT INTO `users` (`UserName`, `UserEmail`, `Password`, `Salt`, `Admin`) " +
                "VALUES ('" + name + "', '" + email + "', '" + encryptedPass + "', '" + salt + "', '" + (admin ? 1 : 0) + "');";

    }
}
