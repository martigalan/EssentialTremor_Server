package iFaces;

import pojos.Doctor;

/**
 * Interface for managing doctor-related operations in the database.
 */
public interface DoctorManager {
    public void addDoctor(Doctor doctor, int userId);
}
