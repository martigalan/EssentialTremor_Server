package pojos;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = 2L;
    private int id;
    private String username;
    private byte[] password;
    private String role;

    public User() {
    }

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

    @Override
    public String toString() {
        return "User{" + "username=" + username + ", password=" + password + ", userId=" + id + ", role=" + role + '}';
    }
}
