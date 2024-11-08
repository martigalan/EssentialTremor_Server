package jdbc;

import iFaces.DoctorNotesManager;
import pojos.DoctorsNote;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JDBCDoctorNotesManager implements DoctorNotesManager {

    private ConnectionManager cM;

    /**
     * Manages the doctorNotes-related operations using a JDBC connection.
     * Uses an instance of {@link ConnectionManager} to interact with the SQLite database.
     *
     * @param cManager The {@link ConnectionManager} instance used for database operations.
     */
    public JDBCDoctorNotesManager(ConnectionManager cManager){
        this.cM = cManager;
    }

    /**
     * Adds a new DoctorsNote to the "DoctorNotes" table in the database.
     * Uses the provided {@link DoctorsNote} object to insert the doctor's note information.
     *
     * @param doctorsNote The {@link DoctorsNote} object containing the doctor's note details (description).
     * @throws SQLException if there is an error during the SQL operation.
     */
    @Override
    public void addDoctorNote(DoctorsNote doctorsNote) {
        try {
            String sql = "INSERT INTO DoctorNotes (description, medical_record_id, doctor_id) SELECT ?, ?, ?";
            PreparedStatement prep = cM.getConnection().prepareStatement(sql);
            prep.setString(1, doctorsNote.getNotes());
            prep.setInt(2, doctorsNote.getMedicalRecordId());
            prep.setInt(3, doctorsNote.getDoctorId());
            prep.executeUpdate();
            prep.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCDoctorNotesManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
