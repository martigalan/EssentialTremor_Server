package iFaces;
import java.sql.Connection;

public interface InterfaceConnectionManager {
    public void disconnect();
    public void createTables();
    public Connection getConnection();
}
