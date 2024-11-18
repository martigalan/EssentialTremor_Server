package jdbc;

import iFaces.PatientManager;
import pojos.Patient;
import pojos.PatientHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JDBCPatientManager implements PatientManager {
    private ConnectionManager cM;

    /**
     * Manages the patient-related operations using a JDBC connection.
     * Uses an instance of {@link ConnectionManager} to interact with the SQLite database.
     *
     * @param cManager The {@link ConnectionManager} instance used for database operations.
     */
    public JDBCPatientManager (ConnectionManager cManager){
        this.cM = cManager;
    }

    /**
     * Adds a new patient to the "Patient" table in the database.
     * Uses the provided {@link Patient} object and user ID to insert the patient's information.
     *
     * @param patient The {@link Patient} object containing the patient's details (name, surname and genetic background).
     * @param userId The user ID to associate with the patient.
     * @throws SQLException if there is an error during the SQL operation.
     */
    @Override
    public void addPatient(Patient patient, int userId) {
        try {
            String sql = "INSERT INTO Patient (name, surname, genetic_background, user_id) SELECT ?, ?, ?, ?";
            PreparedStatement prep = cM.getConnection().prepareStatement(sql);
            prep.setString(1, patient.getName());
            prep.setString(2, patient.getSurname());
            prep.setBoolean(3, patient.getGenetic_background());
            prep.setInt(4, userId);
            prep.executeUpdate();
            prep.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCPatientManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Retrieves a Patient object from the database using the given user ID.
     *
     * @param userId The user ID of the patient to retrieve.
     * @return The Patient object corresponding to the given user ID, or null if no patient is found.
     * @throws SQLException If there is an error executing the SQL query.
     */
    public Patient getPatientByUserId(int userId) throws SQLException {
        Patient patient = null;
        try {
            String query = "SELECT * FROM Patient WHERE user_id = ?";
            PreparedStatement prep = cM.getConnection().prepareStatement(query);
            prep.setInt(1, userId);
            try (ResultSet rs = prep.executeQuery()) {
                if (rs.next()) {
                    patient = new Patient();
                    patient.setId(rs.getInt("id"));
                    patient.setName(rs.getString("name"));
                    patient.setSurname(rs.getString("surname"));
                    patient.setGenetic_background(rs.getBoolean("genetic_background"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving patient by user ID: " + e.getMessage());
            throw e;
        }
        return patient;
    }

    /**
     * Retrieves the ID of a patient from the database using their name and surname.
     *
     * @param name The name of the patient.
     * @param surname The surname of the patient.
     * @return The ID of the patient, or null if no patient is found.
     * @throws SQLException If there is an error executing the SQL query.
     */
    public Integer getIdByNameSurname (String name, String surname) throws SQLException {
        Integer patient_id = null;
        try {
            String query = "SELECT id FROM Patient WHERE name = ? AND surname = ?";
            PreparedStatement prep = cM.getConnection().prepareStatement(query);
            prep.setString(1, name);
            prep.setString(2, surname);
            try (ResultSet rs = prep.executeQuery()) {
                patient_id = rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving ID by name and surname: " + e.getMessage());
            throw e;
        }
        return patient_id;
    }
    /**
     * Retrieves a list of all the patients.
     *
     * @return The Patient list object or null if no patients are found.
     * @throws SQLException If there is an error executing the SQL query.
     */
    public List<Patient> getPatients() throws SQLException {
        List<Patient> pList = new ArrayList<>();
        Patient patient = null;
        try {
            String query = "SELECT id, name, surname FROM Patient";
            PreparedStatement prep = cM.getConnection().prepareStatement(query);

            try (ResultSet rs = prep.executeQuery()) {
                while (rs.next()) {
                    patient = new Patient();
                    patient.setId(rs.getInt("id"));
                    patient.setName(rs.getString("name"));
                    patient.setSurname(rs.getString("surname"));
                    pList.add(patient);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving patient by user ID: " + e.getMessage());
            throw e;
        }
        return pList;
    }
}
