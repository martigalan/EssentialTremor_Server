package jdbc;

import Pojos.State;
import ifaces.StateManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JDBCStateManager implements StateManager {

    private ConnectionManager cM;

    public JDBCStateManager (ConnectionManager cManager){
        this.cM = cManager;
    }

    @Override
    public void addState() {
        for (State state : State.values()) {
            try {
                String sql = "INSERT INTO state (name, description) SELECT ?, ? WHERE NOT EXISTS (SELECT 1 FROM state WHERE name = ? LIMIT 1)";

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
