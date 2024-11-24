package iFaces;

import pojos.Doctor;

import java.sql.SQLException;

/**
 * Interface for managing doctor-related operations in the database.
 */
public interface DoctorManager {
    public void addDoctor(Doctor doctor, int userId);
    public Doctor getDoctorByUserId(int userId) throws SQLException;
    public Doctor getDoctorById(int userId) throws SQLException;
    public Integer getIdByNameSurname(String name, String surname) throws SQLException;

}
