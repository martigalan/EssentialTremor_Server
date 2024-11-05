package jdbc;

import iFaces.PatientManager;
import pojos.Doctor;
import pojos.Patient;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JDBCPatientManager implements PatientManager {
    private ConnectionManager cM;

    public JDBCPatientManager (ConnectionManager cManager){
        this.cM = cManager;
    }

    @Override
    public void addPatient(Patient patient) {
        try {
            String sql = "INSERT INTO Patient (name, surname, genetic_background) SELECT ?, ?, ?";
            PreparedStatement prep = cM.getConnection().prepareStatement(sql);
            prep.setString(1, patient.getName());
            prep.setString(2, patient.getSurname());
            prep.setBoolean(3, patient.getGenetic_background());
            prep.executeUpdate();
            prep.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCPatientManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Patient getPatientByUserId(int userId) throws SQLException {
        Patient patient = null;
        try {
            String query = "SELECT * FROM Patient WHERE user_id = ?";
            PreparedStatement prep = cM.getConnection().prepareStatement(query);
            prep.setInt(1, userId);
            try (ResultSet rs = prep.executeQuery()) {
                if (rs.next()) {
                    patient = new Patient();
                    patient.setId(rs.getInt("id"));
                    patient.setName(rs.getString("name"));
                    patient.setSurname(rs.getString("surname"));
                    patient.setGenetic_background(rs.getBoolean("genetic_background"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving patient by user ID: " + e.getMessage());
            throw e;
        }
        return patient;
    }
}
