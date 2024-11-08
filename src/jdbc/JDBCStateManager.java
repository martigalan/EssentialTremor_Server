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
     * Inserts all the predefined states from the {@link State} enum into the "State" table in the database.
     * Each state is added with its corresponding name and description.
     * If a state already exists (based on its name), it will not be inserted again.
     *
     * @throws SQLException if there is an error during the SQL operation.
     */
    @Override
    public void addState() {
        for (State state : State.values()) {
            try {
                String sql = "INSERT INTO State (name, description) SELECT ?, ? WHERE NOT EXISTS (SELECT 1 FROM state WHERE name = ? LIMIT 1)";

                PreparedStatement prep = cM.getConnection().prepareStatement(sql);
                prep.setString(1, state.name());
                prep.setString(2, state.getDescription());
                prep.setString(3, state.name());
                prep.executeUpdate();
                prep.close();
            } catch (SQLException ex) {
                Logger.getLogger(JDBCStateManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
