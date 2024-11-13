package jdbc;

import iFaces.HasMedicalRecordManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JDBCHasMedicalRecordManager implements HasMedicalRecordManager {

    private ConnectionManager cM;
    /**
     * Manages the N-N relationship between Doctor and MedicalRecord using a JDBC connection.
     * Uses an instance of {@link ConnectionManager} to interact with the SQLite database.
     *
     * @param cManager The {@link ConnectionManager} instance used for database operations.
     */
    public JDBCHasMedicalRecordManager (ConnectionManager cManager){
        this.cM = cManager;
    }

    /**
     * Adds the doctor-medicalRecord association to the table.
     *
     * @param doctor_id
     * @param medical_record_id
     */
    @Override
    public void addMedicalRecordDoctor(int doctor_id, int medical_record_id) {
        try {
            String sql = "INSERT INTO HasMedicalRecord (doctor_id, medical_record_id) SELECT ?, ?";
            PreparedStatement prep = cM.getConnection().prepareStatement(sql);
            prep.setInt(1, doctor_id);
            prep.setInt(2, medical_record_id);
            prep.executeUpdate();
            prep.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCHasMedicalRecordManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
