package iFaces;

/**
 * Interface for managing N-N relationship between Doctor and Patient, it operations in the database.
 */
public interface HasPatientManager {
    void addPatientDoctor(int doctor_id, int patient_id);
}
