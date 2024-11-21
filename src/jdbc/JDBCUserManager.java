package jdbc;

import iFaces.UserManager;
import pojos.User;

import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JDBCUserManager implements UserManager {
    private ConnectionManager cM;

    /**
     * Manages the user-related operations using a JDBC connection.
     * Uses an instance of {@link ConnectionManager} to interact with the SQLite database.
     *
     * @param cManager The {@link ConnectionManager} instance used for database operations.
     */
    public JDBCUserManager(ConnectionManager cManager) {
        this.cM = cManager;
    }

    /**
     * Inserts a new user into the "User" table in the database.
     * The user's username, password (hashed), and role are stored.
     *
     * @param user The {@link User} object containing the user's details (username, password, role).
     * @throws SQLException if there is an error during the SQL operation.
     */
    @Override
    public void addUser(User user) {
        try {
            String sql = "INSERT INTO User (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement prep = cM.getConnection().prepareStatement(sql);
            prep.setString(1, user.getUsername());
            prep.setBytes(2, user.getPassword());
            prep.setString(3, user.getRole());
            prep.executeUpdate();
            prep.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Verifies if a given username already exists in the "User" table.
     *
     * @param username The username to be checked.
     * @return {@code true} if the username exists in the database, {@code false} otherwise.
     * @throws SQLException if there is an error during the SQL query execution.
     */
    @Override
    public boolean verifyUsername(String username, String role) {
        String sql = "SELECT username FROM user WHERE username LIKE ? AND role = ?";
        try {
            PreparedStatement prep = cM.getConnection().prepareStatement(sql);
            prep.setString(1, username);
            prep.setString(2, role);
            ResultSet rs = prep.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Verifies if the provided password matches the stored hashed password for a given username.
     * The password is hashed using the MD5 algorithm and compared to the stored hash.
     *
     * @param username The username to be verified.
     * @param passwordIntroduced The plaintext password input by the user.
     * @return {@code true} if the password matches, {@code false} otherwise.
     * @throws SQLException if there is an error during the SQL query execution.
     * @throws NoSuchAlgorithmException if the MD5 hashing algorithm is not found.
     */
    @Override
    public boolean verifyPassword(String username, String passwordIntroduced) {
        String sql = "SELECT password FROM user WHERE username LIKE ?";
        try {
            PreparedStatement prep = cM.getConnection().prepareStatement(sql);
            prep.setString(1, username);
            ResultSet rs = prep.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves a {@link User} object from the "User" table using the specified user ID.
     *
     * @param id The ID of the user to retrieve.
     * @return The {@link User} object containing the user's details, or {@code null} if no user is found.
     * @throws SQLException if there is an error during the SQL query execution.
     */
    public User getUser(int id) {
        try {
            String sql = "SELECT * FROM USER WHERE id = ?";
            PreparedStatement prep = cM.getConnection().prepareStatement(sql);
            prep.setInt(1, id);
            ResultSet rs = prep.executeQuery();
            if (rs.next()) {
                String username = rs.getString("username");
                byte[] password = rs.getBytes("password");
                String role = rs.getString("role");
                User user = new User(id, username, password, role);
                return user;
            }
            rs.close();
            prep.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCUserManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Retrieves the ID of a user based on the provided username.
     *
     * @param username The username for which the user ID is to be retrieved.
     * @return The ID of the user, or {@code 0} if the user is not found.
     * @throws SQLException if there is an error during the SQL query execution.
     */
    public int getId(String username) {
        String sql1 = "SELECT * FROM USER WHERE username = ?";
        int id = 0;
        try {
            PreparedStatement p = cM.getConnection().prepareStatement(sql1);
            p.setString(1, username);
            ResultSet rs = p.executeQuery();
            id = rs.getInt("id");
        } catch (SQLException ex) {
            Logger.getLogger(JDBCUserManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id;
    }
}
