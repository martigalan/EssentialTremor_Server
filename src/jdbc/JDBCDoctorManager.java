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


}
