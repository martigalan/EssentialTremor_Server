package pojos;

import java.io.Serializable;

public class User implements Serializable {

    //TODO check
    private int id;
    /**
     * Identifier to serialize User
     */
    private static final long serialVersionUID = 2L;
    /**
     * Username to login into the application
     */
    private String username;
    /**
     * Password to login into the application
     */
    private byte[] password;
    /**
     * Users role; patient or doctor
     */
    private String role;

    /**
     * Empty constructor
     */
    public User() {
    }

    //TODO mirar problema ID
    public User(int id, String username, byte[] password, String role) {
        this.id=id;
        this.username=username;
        this.password=password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    /**
     * String representation of User
     * @return String to represent User
     */
    @Override
    public String toString() {
        return "User{" + "username=" + username + ", password=" + password + ", userId=" + id + ", role=" + role + '}';
    }
}
