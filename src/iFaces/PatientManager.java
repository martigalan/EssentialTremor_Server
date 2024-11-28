package iFaces;

import pojos.Patient;

import java.sql.SQLException;
import java.util.List;

/**
 * Interface for managing patient-related operations in the database.
 */
public interface PatientManager {
    public void addPatient(Patient patient, int userId);
    public Patient getPatientByUserId(int userId) throws SQLException;
    public Patient getPatientById(int id) throws SQLException;
    public Integer getIdByNameSurname (String name, String surname) throws SQLException;
    public List<Patient> getPatients() throws SQLException;

}
