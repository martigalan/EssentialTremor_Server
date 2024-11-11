package iFaces;

import pojos.MedicalRecord;

/**
 * Interface for managing medicalRecord-related operations in the database.
 */
public interface MedicalRecordManager {
    public void addMedicalRecord(Integer patient_id, MedicalRecord medicalRecord);
}
