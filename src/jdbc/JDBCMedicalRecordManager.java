package jdbc;

import iFaces.MedicalRecordManager;
import pojos.MedicalRecord;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JDBCMedicalRecordManager implements MedicalRecordManager {

    private ConnectionManager cM;

    /**
     * Manages the medicalRecord-related operations using a JDBC connection.
     * Uses an instance of {@link ConnectionManager} to interact with the SQLite database.
     *
     * @param cManager The {@link ConnectionManager} instance used for database operations.
     */
    public JDBCMedicalRecordManager (ConnectionManager cManager){
        this.cM = cManager;
    }

    /**
     * Adds a new medical record to the "MedicalRecord" table in the database.
     * Uses the provided {@link MedicalRecord} object and user ID to insert the medical's record information.
     *
     * @param medicalRecord The {@link MedicalRecord} object containing the medical's record details (age, weight, height, syptoms, acc and emg).
     * @throws SQLException if there is an error during the SQL operation.
     */
    @Override
    public void addMedicalRecord(Integer patient_id, MedicalRecord medicalRecord) {
        try {
            String sql = "INSERT INTO MedicalRecord (patient_id, age, weight, height, syptoms, acc, emg) SELECT ?, ?, ?, ?, ?, ?, ?";
            PreparedStatement prep = cM.getConnection().prepareStatement(sql);
            prep.setInt(1, patient_id);
            prep.setInt(2, medicalRecord.getAge());
            prep.setDouble(3, medicalRecord.getWeight());
            prep.setDouble(4, medicalRecord.getHeight());
            prep.setString(5, medicalRecord.getSymptoms().get(0));
            /*prep.setString(6, medicalRecord.getAcceleration());
            prep.setString(7, medicalRecord.getEmg());*/
            prep.executeUpdate();
            prep.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCMedicalRecordManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public MedicalRecord findByPatientIdAndDoctorId(int patientId, int doctorId) {
        MedicalRecord record = null;

        try {
            String sql = "SELECT * FROM MedicalRecords WHERE patientId = ? AND doctorId = ?";
            PreparedStatement prep = cM.getConnection().prepareStatement(sql);
            prep.setInt(1, patientId);
            prep.setInt(2, doctorId);

            ResultSet rs = prep.executeQuery();
            if (rs.next()) {
                record = new MedicalRecord();
                record.setId(rs.getInt("id"));
                record.setPatientId(rs.getInt("patientId"));
                record.setDoctorId(rs.getInt("doctorId"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return record;
    }

}
