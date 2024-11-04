package jdbc;

import iFaces.DoctorNotes;
import pojos.DoctorsNote;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JDBCDoctorNotes implements DoctorNotes {

    private ConnectionManager cM;

    public JDBCDoctorNotes (ConnectionManager cManager){
        this.cM = cManager;
    }

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
            Logger.getLogger(JDBCDoctorNotes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
