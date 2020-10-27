package ca.ualberta.cmput301f20t04.bookatmenow;

/**
 * User class
 * @author Jeanne Coleongco
 * @version 0.3
 */
public class User {
    private String userId;
    private String username;
    private String password;
    private String phone;
    private String email;
    private String address;

    /**
     * Constructor for DB handler
     */
    public User() {}

    public String getUserId() {
        return this.userId;
    }

    public void setUserID(String id) {
        this.userId = id;
    }

    /**
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     *
     * @return phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     *
     * @param phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     *
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     *
     * @return address
     */
    public String getAddress() {
        return address;
    }

    /**
     *
     * @param address
     */
    public void setAddress(String address) {
        this.address = address;
    }
}
