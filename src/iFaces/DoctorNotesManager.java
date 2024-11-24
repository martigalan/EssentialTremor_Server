package iFaces;

import pojos.DoctorsNote;

import java.sql.SQLException;
import java.util.List;

/**
 * Interface for managing doctorNotes-related operations in the database.
 */
public interface DoctorNotesManager {
    public void addDoctorNote(DoctorsNote doctorsNote);
    public List<DoctorsNote> getDoctorsNoteByMRID (Integer medicalRecord_id) throws SQLException;
    public DoctorsNote getDoctorsNoteByID (Integer dn_id) throws SQLException;

}
