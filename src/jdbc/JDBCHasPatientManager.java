package jdbc;

import iFaces.HasPatientManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JDBCHasPatientManager implements HasPatientManager {

    private ConnectionManager cM;
    /**
     * Manages the N-N relationship between Doctor and Patient using a JDBC connection.
     * Uses an instance of {@link ConnectionManager} to interact with the SQLite database.
     *
     * @param cManager The {@link ConnectionManager} instance used for database operations.
     */
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
            Logger.getLogger(JDBCHasPatientManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
