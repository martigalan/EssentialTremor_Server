package jdbc;

import iFaces.DoctorNotesManager;
import pojos.DoctorsNote;
import pojos.State;
import pojos.Treatment;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
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
            prep.setString(4, doctorsNote.getDateAsString());
            prep.setInt(5, doctorsNote.getState().getId());
            prep.setInt(6, doctorsNote.getTreatment().getId());
            prep.executeUpdate();
            prep.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCDoctorNotesManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Retrieves a {@link List<DoctorsNote>} object with provides all the doctor's note information.
     * Create a {@link List<DoctorsNote>} object to insert the doctor's note information obtain from the database.
     *
     * @param medicalRecord_id id of medicalRecord, related to the doctor's note.
     * @throws SQLException if there is an error during the SQL operation.
     */
    public List<DoctorsNote> getDoctorsNoteByMRID (Integer medicalRecord_id) throws SQLException {
        List<DoctorsNote> dns = null;
        try {
            String query = "SELECT * FROM DoctorNotes WHERE medical_record_id = ?";
            PreparedStatement prep = cM.getConnection().prepareStatement(query);
            prep.setInt(1, medicalRecord_id);

            ResultSet rs = prep.executeQuery();
            while (rs.next()) {
                DoctorsNote doctorsNote = new DoctorsNote();
                doctorsNote.setId(rs.getInt("id"));
                doctorsNote.setDateAsString(rs.getString("date"));
                dns.add(doctorsNote);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving doctorsNote by ID: " + e.getMessage());
            throw e;
        }
        return dns;
    }

    /**
     * Retrieves a DoctorsNote from the database with the ID.
     * @param dn_id Id of the note we want to get
     * @return DoctorsNote with the id
     * @throws SQLException in case of database error
     */
    public DoctorsNote getDoctorsNoteByID (Integer dn_id) throws SQLException {
        DoctorsNote dn = null;
        try{
            String query = "SELECT * FROM DoctorNotes WHERE id = ?";
            PreparedStatement prep = cM.getConnection().prepareStatement(query);
            prep.setInt(1, dn_id);

            try (ResultSet rs = prep.executeQuery()) {
                if (rs.next()) {
                    dn = new DoctorsNote();
                    dn.setId(rs.getInt("id"));
                    dn.setNotes(rs.getString("description"));
                    dn.setMedicalRecordId(rs.getInt("medical_record_id"));
                    dn.setDoctorId(rs.getInt("doctor_id"));
                    dn.setDateAsString(rs.getString("date"));
                    dn.setState(State.valueOf(rs.getString("state")));
                    dn.setTreatment(Treatment.valueOf(rs.getString("treatment")));
                } else {
                    System.out.println("No Doctor Notes found for ID: " + dn_id);
                }
            }
            return dn;
        } catch (SQLException e) {
            System.out.println("Error retrieving MedicalRecord by ID: " + e.getMessage());
            throw e;
        }
    }
}
