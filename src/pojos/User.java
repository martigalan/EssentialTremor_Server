package pojos;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = 2L;
    private int id;
    public String username;
    public byte[] password;

    public User(int id) {
        super();
    }

    public User(int id, String username, byte[] password) {
        this.id=id;
        this.username=username;
        this.password=password;
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

    @Override
    public String toString() {
        return "User{" + "username=" + username + ", password=" + password + ", userId=" + id + '}';
    }
}
