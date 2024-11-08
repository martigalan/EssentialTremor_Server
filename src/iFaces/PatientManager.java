package iFaces;

import pojos.Patient;

/**
 * Interface for managing patient-related operations in the database.
 */
public interface PatientManager {
    public void addPatient(Patient patient, int userId);
}
