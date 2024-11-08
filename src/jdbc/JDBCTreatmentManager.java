package jdbc;

import iFaces.TreatmentManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import pojos.Treatment;

public class JDBCTreatmentManager implements TreatmentManager {
    private ConnectionManager cM;

    /**
     * Manages the treatment-related operations using a JDBC connection.
     * Uses an instance of {@link ConnectionManager} to interact with the SQLite database.
     *
     * @param cManager The {@link ConnectionManager} instance used for database operations.
     */
    public JDBCTreatmentManager(ConnectionManager cManager) {
        this.cM = cManager;
    }

    /**
     * Inserts all the predefined states from the {@link Treatment} enum into the "Treatment" table in the database.
     * Each treatment is added with its corresponding name and description.
     * If a state already exists (based on its name), it will not be inserted again.
     *
     * @throws SQLException if there is an error during the SQL operation.
     */
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
                Logger.getLogger(JDBCTreatmentManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
