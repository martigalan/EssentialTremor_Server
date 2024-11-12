package jdbc;

import iFaces.HasPatientManager;
import pojos.Patient;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JDBCHasPatientManager implements HasPatientManager {
    private ConnectionManager cM;
    public JDBCHasPatientManager (ConnectionManager cManager){
        this.cM = cManager;
    }

    /**
     * Adds the doctor-patient association to the table.
     *
     * @param doctor_id
     * @param patient_id
     */
    @Override
    public void addPatientDoctor(int doctor_id, int patient_id) {
        try {
            String sql = "INSERT INTO HasPatient (doctor_id, patient_id) SELECT ?, ?";
            PreparedStatement prep = cM.getConnection().prepareStatement(sql);
            prep.setInt(1, doctor_id);
            prep.setInt(2, patient_id);
            prep.executeUpdate();
            prep.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCPatientManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
