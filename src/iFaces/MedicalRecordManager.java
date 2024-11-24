package iFaces;

import pojos.MedicalRecord;

import java.sql.SQLException;
import java.util.List;

/**
 * Interface for managing medicalRecord-related operations in the database.
 */
public interface MedicalRecordManager {
    public void addMedicalRecord(Integer patient_id, MedicalRecord medicalRecord);
    public List<MedicalRecord> findByPatientId (int patient_id);
    public MedicalRecord getMedicalRecordByID (Integer medicalRecord_id) throws SQLException;

}
