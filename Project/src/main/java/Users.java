public interface Users extends DBObject<User> {
    void setUserEmail(String userEmail);
    void setUserName(String userName);
    void setPassword(String pass); //Takes password from user and turns it into encrypted string and corresponding salt to keep in the DB
    void setAdmin(Boolean admin);
    String getUser();
    Boolean verifyPass(String pass, String encrypted, String salt); //Password from user verified to the encrypted password in the DB and the corresponding salt stored in the DB


}
