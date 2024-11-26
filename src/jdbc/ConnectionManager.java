package jdbc;

import iFaces.InterfaceConnectionManager;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionManager implements InterfaceConnectionManager {

    private Connection c = null;

    /**
     * Establishes a connection to the SQLite database located at "./db/EssentialTremor.db".
     * Ensures the necessary database directory exists and creates the tables if not already created.
     * Handles exceptions related to class loading, SQL errors, and I/O issues.
     */
    public ConnectionManager() {
        try {
            Class.forName("org.sqlite.JDBC");
            File dbDirectory = new File("./db");

            // Verify if the directory exists or if it can be created
            if (!dbDirectory.exists()) {
                if (!dbDirectory.mkdirs()) {
                    throw new IOException("No se pudo crear el directorio ./db");
                }
            }

            // Establish the connection to the database
            c = DriverManager.getConnection("jdbc:sqlite:./db/EssentialTremor.db");
            c.createStatement().execute("PRAGMA foreign_keys=ON");
            System.out.println("Conexión a la base de datos abierta.");
            this.createTables();
        } catch (ClassNotFoundException e) {
            System.out.println("Librerías no cargadas.");
        } catch (SQLException ex) {
            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Creates the necessary tables for the database, including:
     * <ul>
     *     <li>Doctor</li>
     *     <li>State</li>
     *     <li>Treatment</li>
     *     <li>User</li>
     *     <li>MedicalRecord</li>
     *     <li>Patient</li>
     *     <li>DoctorNotes</li>
     *     <li>HasPatient</li>
     *     <li>HasNotes</li>
     * </ul>
     * If the tables already exist, it logs a message without throwing an exception.
     *
     * @throws SQLException if there is an error in SQL execution.
     */
    @Override
    public void createTables() {
        try {
            Statement stmt = c.createStatement();
            String table = "CREATE TABLE Doctor (" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "    name TEXT NOT NULL," +
                    "    surname TEXT NOT NULL," +
                    "    user_id INTEGER," +
                    "    FOREIGN KEY (user_id) REFERENCES User(id)" +
                    ");";
            stmt.executeUpdate(table);
            String table1= "CREATE TABLE State (" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "    name TEXT NOT NULL," +
                    "    description TEXT NOT NULL" +
                    ");";
            stmt.executeUpdate(table1);
            String table2 = "CREATE TABLE Treatment (" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "    name TEXT NOT NULL," +
                    "    description TEXT NOT NULL" +
                    ");";
            stmt.executeUpdate(table2);
            String table3 = "CREATE TABLE User (" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "    username TEXT NOT NULL," +
                    "    password BLOB NOT NULL," +
                    "    role TEXT NOT NULL" +
                    ");";
            stmt.executeUpdate(table3);
            String table4 = "CREATE TABLE MedicalRecord (" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "    patient_id INTEGER," +
                    "    age INTEGER NOT NULL," +
                    "    weight REAL NOT NULL," +
                    "    height INTEGER NOT NULL," +
                    "    symptoms TEXT," +
                    "    acc TEXT," +
                    "    emg TETX," +
                    "    date TEXT," +
                    "    FOREIGN KEY (patient_id) REFERENCES Patient(id) ON DELETE CASCADE" +
                    ");";
            stmt.executeUpdate(table4);
            String table5 = "CREATE TABLE Patient (" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "    name TEXT NOT NULL," +
                    "    surname TEXT NOT NULL," +
                    "    genetic_background BOOLEAN," +
                    "    user_id INTEGER," +
                    "    FOREIGN KEY (user_id) REFERENCES User(id)" +
                    ");";
            stmt.executeUpdate(table5);
            String table6 = "CREATE TABLE DoctorNotes (" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "    description TEXT NOT NULL," +
                    "    medical_record_id INTEGER," +
                    "    doctor_id INTEGER," +
                    "    date TEXT," +
                    "    state INTEGER," +
                    "    treatment INTEGER," +
                    "    FOREIGN KEY (medical_record_id) REFERENCES MedicalRecord(id)," +
                    "    FOREIGN KEY (doctor_id) REFERENCES Doctor(id)" +
                    "    FOREIGN KEY (state) REFERENCES State(id), " +
                    "    FOREIGN KEY (treatment) REFERENCES Treatment(id), " +
                    ");";
            stmt.executeUpdate(table6);
            String table7 = "CREATE TABLE HasPatient (" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "    doctor_id INTEGER," +
                    "    patient_id INTEGER," +
                    "    FOREIGN KEY (doctor_id) REFERENCES Doctor(id)," +
                    "    FOREIGN KEY (patient_id) REFERENCES Patient(id)" +
                    ");";
            stmt.executeUpdate(table7);
            String table8 = "CREATE TABLE HasMedicalRecord (" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "    doctor_id INTEGER," +
                    "    medical_record_id INTEGER," +
                    "    FOREIGN KEY (doctor_id) REFERENCES Doctor(id)," +
                    "    FOREIGN KEY (medical_record_id) REFERENCES MedicalRecord(id)" +
                    ");";
            stmt.executeUpdate(table8);
        } catch (SQLException e) {
            // Check if the exception is because the tables already exist
            if (e.getMessage().contains("already exist")) {
                System.out.println("Tables already created.");
                return;
            }
            System.out.println("Database error.");
            e.printStackTrace();
        }
    }

    /**
     * Provides the current connection instance to the SQLite database.
     * @return The active {@link Connection} object to the SQLite database.
     */
    @Override
    public Connection getConnection() {
        return c;
    }

    /**
     * Closes the active connection to the SQLite database.
     * Logs and handles any {@link SQLException} that might occur during the disconnection process.
     */
    @Override
    public void disconnect() {
        try {
            c.close();
        } catch (SQLException e) {
            System.out.println("Database error.");
            e.printStackTrace();
        }
    }
}