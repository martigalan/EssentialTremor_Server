package jdbc;

import iFaces.PatientManager;
import pojos.Patient;
import java.sql.PreparedStatement;
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
            Logger.getLogger(JDBCStateManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
