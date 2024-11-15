package iFaces;

import java.sql.SQLException;

/**
 * Interface for managing N-N relationship between Doctor and Patient, it operations in the database.
 */
public interface HasPatientManager {
    void addPatientDoctor(int doctor_id, int patient_id);
    Boolean isAlreadyCreated (int doctor_id, int patient_id) throws SQLException;
}
