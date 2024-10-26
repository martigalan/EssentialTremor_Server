package iFaces;

import pojos.User;

public interface UserManager {
    public void addUser(User user);
    public boolean verifyPassword(String username, String passwordIntroduced);
    public boolean verifyUsername(String username);
}
