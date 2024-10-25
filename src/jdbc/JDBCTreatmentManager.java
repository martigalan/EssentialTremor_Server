package jdbc;

import iFaces.TreatmentManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import pojos.Treatment;

public class JDBCTreatmentManager implements TreatmentManager {
    private ConnectionManager cM;

    public JDBCTreatmentManager(ConnectionManager cManager) {
        this.cM = cManager;
    }

    @Override
    public void addTreatment() {
        for (Treatment treatment : Treatment.values()) {
            try {
                String sql = "INSERT INTO treatment (name, description) SELECT ?, ? WHERE NOT EXISTS (SELECT 1 FROM treatment WHERE name = ? LIMIT 1)";

                PreparedStatement prep = cM.getConnection().prepareStatement(sql);
                prep.setString(1, treatment.name());
                prep.setString(2, treatment.getDescription());
                prep.setString(3, treatment.name());
                prep.executeUpdate();
                prep.close();
            } catch (SQLException ex) {
                Logger.getLogger(JDBCStateManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
