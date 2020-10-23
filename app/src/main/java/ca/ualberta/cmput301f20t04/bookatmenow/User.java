package ca.ualberta.cmput301f20t04.bookatmenow;

import java.util.regex.*;

public class User {
    private String username; // can make unique w/ firebase once it's set up
    private String password;
    private String phone;
    private String email;
    private String address;

    /**
     * constructs basic User
     * @param username
     * @param password
     * @param email
     */
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    /**
     * constructs user with optional phone #
     * @param username
     * @param password
     * @param phoneOrAddress
     * @param email
     */
    public User(String username, String password, String phoneOrAddress, String email) {
        this.username = username;
        this.password = password;

        final Pattern p = Pattern.compile("[0-9]+");
        if (p.matcher(phoneOrAddress).matches()) {
            this.phone = phoneOrAddress;
        } else {
            this.address = phoneOrAddress;
        }

        this.email = email;
    }

    /**
     * constructs user with everything
     * @param username
     * @param password
     * @param phone
     * @param email
     * @param address
     */
    public User(String username, String password, String phone, String email, String address) {
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.email = email;
        this.address = address;
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
