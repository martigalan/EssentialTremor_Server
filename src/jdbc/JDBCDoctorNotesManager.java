package jdbc;

import iFaces.DoctorNotesManager;
import pojos.Doctor;
import pojos.DoctorsNote;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
            String sql = "INSERT INTO DoctorNotes (description, medical_record_id, doctor_id, date, state, treatment) SELECT ?, ?, ?, ?, ?, ?";
            PreparedStatement prep = cM.getConnection().prepareStatement(sql);
            prep.setString(1, doctorsNote.getNotes());
            prep.setInt(2, doctorsNote.getMedicalRecordId());
            prep.setInt(3, doctorsNote.getDoctorId());
            prep.setString(4, doctorsNote.getDate());
            //TODO meter state and treatment
            prep.setString(5, doctorsNote.getState());
            prep.setString(6, doctorsNote.getTreatment());
            prep.executeUpdate();
            prep.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCDoctorNotesManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public DoctorsNote getDoctorsNoteByID (Integer medicalRecord_id) throws SQLException {
        DoctorsNote dn = null;
        try {
            String query = "SELECT * FROM DoctorNotes WHERE medical_record_id = ?";
            PreparedStatement prep = cM.getConnection().prepareStatement(query);
            prep.setInt(1, medicalRecord_id);

            try (ResultSet rs = prep.executeQuery()) {
                if (rs.next()) {
                    dn = new DoctorsNote();
                    dn.setNotes(rs.getString("description"));
                    //TODO meterlo en la bbdd
                    dn.setState(rs.);
                    dn.setTreatment(rs.);
                } else {
                    System.out.println("No doctors note found for medical record ID: " + medicalRecord_id);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving doctorsNote by ID: " + e.getMessage());
            throw e;
        }
        return dn;
    }
}
