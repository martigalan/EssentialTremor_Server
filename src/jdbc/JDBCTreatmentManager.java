package jdbc;

import iFaces.TreatmentManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
     * Inserts the default treatments into the `Treatment` table if they do not already exist in the database.
     * The default treatments are:
     * <ul>
     *     <li>SURGERY: The patient needs surgery.</li>
     *     <li>PROPRANOLOL: The patient needs pharmacology treatment, specifically Propranolol.</li>
     *     <li>PRIMIDONE: The patient needs pharmacology treatment, specifically Primidone.</li>
     * </ul>
     *
     * Each treatment is inserted only if a treatment with the same name does not already exist in the table.
     * The `WHERE NOT EXISTS` clause is used to prevent duplicates.
     *
     * @throws SQLException If an error occurs while executing the SQL query in the database.
     */
    public void addTreatment() {
        try {
            // SQL to insert each default treatment into the Treatment table individually
            String[] treatments = {
                    "INSERT INTO Treatment (id, name, description) SELECT 1, 'SURGERY', 'The patient needs surgery.' WHERE NOT EXISTS (SELECT 1 FROM Treatment WHERE name = 'SURGERY' LIMIT 1);",
                    "INSERT INTO Treatment (id, name, description) SELECT 2, 'PROPRANOLOL', 'The patient needs pharmacology treatment, specifically Propranolol.' WHERE NOT EXISTS (SELECT 1 FROM Treatment WHERE name = 'PROPRANOLOL' LIMIT 1);",
                    "INSERT INTO Treatment (id, name, description) SELECT 3, 'PRIMIDONE', 'The patient needs pharmacology treatment, specifically Primidone.' WHERE NOT EXISTS (SELECT 1 FROM Treatment WHERE name = 'PRIMIDONE' LIMIT 1);",
                    "INSERT INTO Treatment (id, name, description) SELECT 4, 'NONE', 'The patient does not need any treatment.' WHERE NOT EXISTS (SELECT 1 FROM Treatment WHERE name = 'NONE' LIMIT 1);"
            };

            // Loop through each treatment and execute the respective insert
            for (String sql : treatments) {
                PreparedStatement prep = cM.getConnection().prepareStatement(sql);
                prep.executeUpdate();
                prep.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDBCTreatmentManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
