package jdbc;

import iFaces.UserManager;
import pojos.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JDBCUserManager implements UserManager {
    private ConnectionManager cM;

    public JDBCUserManager(ConnectionManager cManager) {
        this.cM = cManager;
    }

    @Override
    public void addUser(User user) {
        try {
            String sql = "INSERT INTO User (username, password) VALUES (?, ?)";
            PreparedStatement prep = cM.getConnection().prepareStatement(sql);
            prep.setString(1, user.getUsername());
            prep.setBytes(2, user.getPassword());
            prep.executeUpdate();
            prep.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean verifyUsername(String username) {
        String sql = "SELECT username FROM user WHERE username LIKE ?";
        try {
            PreparedStatement prep = cM.getConnection().prepareStatement(sql);
            prep.setString(1, username);
            ResultSet rs = prep.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean verifyPassword(String username, String passwordIntroduced) {
        String sql = "SELECT password FROM user WHERE username LIKE ?";
        try {
            PreparedStatement prep = cM.getConnection().prepareStatement(sql);
            prep.setString(1, username);
            ResultSet rs = prep.executeQuery();
            if (rs.next()) {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(passwordIntroduced.getBytes());
                byte[] hashIntroduced = md.digest();
                byte[] hashSaved = rs.getBytes("password");
                return Arrays.equals(hashIntroduced, hashSaved);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException ex) {
        }
        return false;
    }

    public User getUser(int id) {
        try {
            String sql = "SELECT * FROM USER WHERE id = ?";
            PreparedStatement prep = cM.getConnection().prepareStatement(sql);
            prep.setInt(1, id);
            ResultSet rs = prep.executeQuery();
            if (rs.next()) {
                String username = rs.getString("username");
                byte[] password = rs.getBytes("password");
                User user = new User(id, username, password);
                return user;
            }
            rs.close();
            prep.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCUserManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

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
