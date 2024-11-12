package iFaces;

/**
 * Interface for managing N-N relationship between Doctor and MedicalRecord, it operations in the database.
 */
public interface HasMedicalRecordManager {
    public void addMedicalRecordDoctor(int doctor_id, int medical_record_id);
}
