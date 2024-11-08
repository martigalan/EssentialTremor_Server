package iFaces;

import java.sql.Connection;

/**
 * Interface for managing connection operations in the database.
 */
public interface InterfaceConnectionManager {
    public void disconnect();
    public void createTables();
    public Connection getConnection();
}
