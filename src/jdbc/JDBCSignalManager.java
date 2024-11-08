package jdbc;

import data.ACC;
import data.EMG;
import iFaces.SignalManager;

public class JDBCSignalManager implements SignalManager {
    private ConnectionManager cManager;

    /**
     * Manages the doctor-related operations using a JDBC connection.
     * Uses an instance of {@link ConnectionManager} to interact with the SQLite database.
     *
     * @param cManager The {@link ConnectionManager} instance used for database operations.
     */
    public JDBCSignalManager(ConnectionManager cManager) {
        this.cManager = cManager;
    }

    public void saveEMGSignal(EMG emg) {
        /*String query = "INSERT INTO emg_signals (signal_data, filename, path, timestamp) VALUES (?, ?, ?, ?)";
        try (Connection conn = cManager.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {
            prep.setString(1, emg.listToString(emg.getSignalData()));
            prep.setString(2, emg.getFilename());
            prep.setString(3, emg.getPath());
            prep.setString(4, emg.listToString(emg.getTimestamp()));
            prep.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void saveACCSignal(ACC acc) {
        /*String query = "INSERT INTO acc_signals (signal_data, filename, path, timestamp) VALUES (?, ?, ?, ?)";
        try (Connection conn = cManager.getConnection();
             PreparedStatement prep = conn.prepareStatement(query)) {
            prep.setString(1, acc.listToString(acc.getSignalData()));
            prep.setString(2, acc.getFilename());
            prep.setString(3, acc.getPath());
            prep.setString(4, acc.listToString(acc.getTimestamp()));
            prep.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
    }

    public EMG getEMGSignal(int id) {
        return null;
    }

    @Override
    public ACC getACCSignal(int id) {
        return null;
    }

    @Override
    public void updateEMGSignal(EMG emg) {

    }

    @Override
    public void deleteEMGSignal(int id) {

    }

    @Override
    public void deleteACCSignal(int id) {

    }
}
