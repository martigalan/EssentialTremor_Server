package jdbc;

import iFaces.DoctorManager;
import pojos.Doctor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JDBCDoctorManager implements DoctorManager {

    private ConnectionManager cM;

    /**
     * Manages the doctor-related operations using a JDBC connection.
     * Uses an instance of {@link ConnectionManager} to interact with the SQLite database.
     *
     * @param cManager The {@link ConnectionManager} instance used for database operations.
     */
    public JDBCDoctorManager(ConnectionManager cManager){
        this.cM = cManager;
    }

    /**
     * Adds a new doctor to the "Doctor" table in the database.
     * Uses the provided {@link Doctor} object and user ID to insert the doctor's information.
     *
     * @param doctor The {@link Doctor} object containing the doctor's details (name and surname).
     * @param userId The user ID to associate with the doctor.
     * @throws SQLException if there is an error during the SQL operation.
     */
    @Override
    public void addDoctor(Doctor doctor, int userId) {
        try {
            String sql = "INSERT INTO Doctor (name, surname, user_id) SELECT ?, ?, ?";
            PreparedStatement prep = cM.getConnection().prepareStatement(sql);
            prep.setString(1, doctor.getName());
            prep.setString(2, doctor.getSurname());
            prep.setInt(3, userId);
            prep.executeUpdate();
            prep.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCDoctorManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Retrieves a Doctor object from the database based on the user ID.
     * The Doctor is fetched by matching the given user ID with the doctor's user ID in the database.
     *
     * @param userId The user ID of the doctor to retrieve.
     * @return A Doctor object populated with data from the database, or null if no doctor is found.
     * @throws SQLException If there is an error accessing the database.
     */
    public Doctor getDoctorByUserId(int userId) throws SQLException {
        Doctor doctor = null;
        try {
            String query = "SELECT * FROM Doctor WHERE user_id = ?";
            PreparedStatement prep = cM.getConnection().prepareStatement(query);
            prep.setInt(1, userId);

            try (ResultSet rs = prep.executeQuery()) {
                if (rs.next()) {
                    doctor = new Doctor();
                    doctor.setUserId(rs.getInt("id"));
                    doctor.setName(rs.getString("name"));
                    doctor.setSurname(rs.getString("surname"));
                } else {
                    System.out.println("No doctor found for user ID: " + userId);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving doctor by user ID: " + e.getMessage());
            throw e;
        }
        return doctor;
    }
    /**
     * Retrieves an id from the database based on the name and surname.
     * The id is fetched by matching the given name and surname with the doctor's parameters in the database.
     *
     * @param name name of the doctor whose id we want to retrieve.
     * @param surname surname of the doctor whose id we want to retrieve.
     * @return An id Ineteger, or null if no doctor is found.
     * @throws SQLException If there is an error accessing the database.
     */
    public Integer getIdByNameSurname(String name, String surname) throws SQLException {
        Integer id = null;
        try {
            String query = "SELECT id FROM Doctor WHERE name = ? AND surname = ?";
            PreparedStatement prep = cM.getConnection().prepareStatement(query);
            prep.setString(1, name);
            prep.setString(2, surname);

            try (ResultSet rs = prep.executeQuery()) {
                if (rs.next()) {
                    id = rs.getInt("id");
                } else {
                    System.out.println("No doctor found for name and surname: " + name + surname);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving doctor by name and surname: " + e.getMessage());
            throw e;
        }
        return id;
    }
}
