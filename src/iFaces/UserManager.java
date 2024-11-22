package iFaces;
import pojos.User;

/**
 * Interface for managing user-related operations in the database.
 */
public interface UserManager {
    public void addUser(User user);
    public boolean verifyPassword(String username, String passwordIntroduced);
    public boolean verifyUsername(String username, String role);
    public User getUser(int id);
    public int getId(String username);
}
