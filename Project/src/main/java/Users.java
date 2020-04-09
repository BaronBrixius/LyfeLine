public interface Users extends DBObject<User> {
    void setEmail(String email);
    void setName(String name);
    void setPassword(String pass); //Takes password from user and turns it into encrypted string and corresponding salt to keep in the DB
    void isAdmin(Boolean admin);
    String getUser(String email);
    Boolean verifyPass(String pass, String encrypted, String salt); //Password from user verified to the encrypted password in the DB and the corresponding salt stored in the DB


}
