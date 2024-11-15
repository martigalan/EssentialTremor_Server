package jdbc;

import pojos.State;
import iFaces.StateManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JDBCStateManager implements StateManager {

    private ConnectionManager cM;

    /**
     * Manages the state-related operations using a JDBC connection.
     * Uses an instance of {@link ConnectionManager} to interact with the SQLite database.
     *
     * @param cManager The {@link ConnectionManager} instance used for database operations.
     */
    public JDBCStateManager (ConnectionManager cManager){
        this.cM = cManager;
    }

    /**
     * Inserts the default states into the `State` table if they do not already exist in the database.
     * The default states are:
     * <ul>
     *     <li>STABLE: The patient is stable and under control.</li>
     *     <li>GOOD: The patient is in good health.</li>
     *     <li>BAD: The patient is in bad condition.</li>
     *     <li>CLOSED: The case is closed, no further action is needed.</li>
     * </ul>
     *
     * Each state is inserted only if a state with the same name does not already exist in the table.
     * The `WHERE NOT EXISTS` clause is used to prevent duplicates.
     *
     * @throws SQLException If an error occurs while executing the SQL query in the database.
     */
    @Override
    public void addState() {
        try {
            // SQL to insert each default state into the State table individually
            String[] states = {
                    "INSERT INTO State (id, name, description) SELECT 1, 'STABLE', 'The condition didn''t evolve. The patient is stable and under control.' WHERE NOT EXISTS (SELECT 1 FROM State WHERE name = 'STABLE' LIMIT 1);",
                    "INSERT INTO State (id, name, description) SELECT 2, 'GOOD', 'The condition made good progress. The patient is in good health.' WHERE NOT EXISTS (SELECT 1 FROM State WHERE name = 'GOOD' LIMIT 1);",
                    "INSERT INTO State (id, name, description) SELECT 3, 'BAD', 'The condition worsened. The patient is in bad condition.' WHERE NOT EXISTS (SELECT 1 FROM State WHERE name = 'BAD' LIMIT 1);",
                    "INSERT INTO State (id, name, description) SELECT 4, 'CLOSED', 'The case is closed, due to the patient moving hospitals, dying, or any other cause that would cause the patient to stop following the condition''s progress.' WHERE NOT EXISTS (SELECT 1 FROM State WHERE name = 'CLOSED' LIMIT 1);"
            };

            // Loop through each state and execute the respective insert
            for (String sql : states) {
                PreparedStatement prep = cM.getConnection().prepareStatement(sql);
                prep.executeUpdate();
                prep.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDBCStateManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
