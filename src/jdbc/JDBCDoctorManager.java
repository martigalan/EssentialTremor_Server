package jdbc;

import iFaces.DoctorManager;
import pojos.Doctor;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JDBCDoctorManager implements DoctorManager {

    private ConnectionManager cM;

    public JDBCDoctorManager (ConnectionManager cManager){
        this.cM = cManager;
    }

    @Override
    public void addDoctor(Doctor doctor) {
        try {
            String sql = "INSERT INTO Doctor (name, surname) SELECT ?, ?";
            PreparedStatement prep = cM.getConnection().prepareStatement(sql);
            prep.setString(1, doctor.getName());
            prep.setString(2, doctor.getSurname());
            prep.executeUpdate();
            prep.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCStateManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
