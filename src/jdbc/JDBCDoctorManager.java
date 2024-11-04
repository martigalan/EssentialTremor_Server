package jdbc;

import iFaces.DoctorManager;
import pojos.Doctor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JDBCDoctorManager implements DoctorManager {

    private ConnectionManager cM;

    public JDBCDoctorManager (ConnectionManager cManager){
        this.cM = cManager;
    }

    @Override
    public void addDoctor(Doctor doctor) {
        try {
            String sql = "INSERT INTO Doctor (name, surname) SELECT ?, ?";
            PreparedStatement prep = cM.getConnection().prepareStatement(sql);
            prep.setString(1, doctor.getName());
            prep.setString(2, doctor.getSurname());
            prep.executeUpdate();
            prep.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCDoctorManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Doctor getDoctorByUserId(int userId) throws SQLException {
        Doctor doctor = null;
        try {
            String query = "SELECT * FROM Doctor WHERE user_id = ?";
            PreparedStatement prep = cM.getConnection().prepareStatement(query);
            prep.setInt(1, userId);
            try (ResultSet rs = prep.executeQuery()) {
                if (rs.next()) {
                    doctor = new Doctor();
                    doctor.setId(rs.getInt("id"));
                    doctor.setName(rs.getString("name"));
                    doctor.setSurname(rs.getString("surname"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving doctor by user ID: " + e.getMessage());
            throw e;
        }
        return doctor;
    }

}
