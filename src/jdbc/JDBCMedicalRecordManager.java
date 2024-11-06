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

    public JDBCMedicalRecordManager (ConnectionManager cManager){
        this.cM = cManager;
    }

    @Override
    public void addMedicalRecord(MedicalRecord medicalRecord) {
        try {
            String sql = "INSERT INTO MedicalRecord (age, weight, height, syptoms, acc, emg) SELECT ?, ?, ?, ?, ?, ?";
            PreparedStatement prep = cM.getConnection().prepareStatement(sql);
            prep.setInt(1, medicalRecord.getAge());
            prep.setDouble(2, medicalRecord.getWeight());
            prep.setDouble(3, medicalRecord.getHeight());
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
