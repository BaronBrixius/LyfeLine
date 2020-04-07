import java.sql.ResultSet;
import java.sql.SQLException;

//Just example implement first and foremost to show the encryption/salting method to use.
//Of course all kinds of exception handling needed for inputs, such as email.
public class Users implements User {
   private String email;
   private String name;
   private  String encryptedPass;
   private  String salt;
   private String admin;

    public Users(){}//I keep this one and the setters if we want to use them or test individual attributes in a main


    public Users(String email, String name, String password, Boolean admin){
        this.email = email;
        this.name = name;
        isAdmin(admin);
        setPassword(password);    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public void setName(String name) {
       this.name = name;    }


    @Override
    public void isAdmin(Boolean admin) {
        if (admin==true)
            this.admin = "true";
        else
            this.admin = "false";
    }

    @Override
    public String getUser(String email) { //Not sure about this one
        return null;
    }

    @Override
    public void setPassword(String pass) {
        this.salt  = PasswordUtils.getSalt(30); //Length of the salt string
        this.encryptedPass = PasswordUtils.generateSecurePassword(pass, this.salt);

    }
    @Override
    public Boolean verifyPass(String pass, String encrypted, String salt) {
        String givenPassword = pass;
        String DBSecurePassword = encrypted; //the one from DB, created with setPassword
        String DBsalt = salt; //the one in DB associated with the encrypted password there and created with setPassword
        return PasswordUtils.verifyUserPassword(givenPassword, DBSecurePassword, DBsalt);
    }

    @Override
    public Object createFromDB(ResultSet rs) throws SQLException {
        return null;
    }

    @Override
    public String getInsertQuery() throws SQLException {
        return "INSERT INTO `users` (`UserName`, `UserEmail`, `UserPassword`, `UserSalt`, , `Admin`) " +
                "VALUES ('" + name + "', '" + email + "', '" + encryptedPass + "', '" + salt +  "', '" + admin +"');";

    }
}
