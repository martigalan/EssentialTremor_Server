package jdbc;

import iFaces.MedicalRecordManager;
import pojos.DoctorsNote;
import pojos.MedicalRecord;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JDBCMedicalRecordManager implements MedicalRecordManager {

    private ConnectionManager cM;

    /**
     * Manages the medicalRecord-related operations using a JDBC connection.
     * Uses an instance of {@link ConnectionManager} to interact with the SQLite database.
     *
     * @param cManager The {@link ConnectionManager} instance used for database operations.
     */
    public JDBCMedicalRecordManager (ConnectionManager cManager){
        this.cM = cManager;
    }

    /**
     * Adds a new medical record to the "MedicalRecord" table in the database.
     * Uses the provided {@link MedicalRecord} object and user ID to insert the medical's record information.
     *
     * @param medicalRecord The {@link MedicalRecord} object containing the medical's record details (age, weight, height, syptoms, acc and emg).
     * @throws SQLException if there is an error during the SQL operation.
     */
    @Override
    public void addMedicalRecord(Integer patient_id, MedicalRecord medicalRecord) {
        try {
            String sql = "INSERT INTO MedicalRecord (patient_id, age, weight, height, symptoms, acc, emg, date) SELECT ?, ?, ?, ?, ?, ?, ?, ?";
            PreparedStatement prep = cM.getConnection().prepareStatement(sql);
            prep.setInt(1, patient_id);
            prep.setInt(2, medicalRecord.getAge());
            prep.setDouble(3, medicalRecord.getWeight());
            prep.setInt(4, medicalRecord.getHeight());
            prep.setString(5, medicalRecord.getSymptomsAsString());
            prep.setString(6, medicalRecord.getAcceleration());
            prep.setString(7, medicalRecord.getEmg());
            prep.setString(8, medicalRecord.getDateAsString());
            prep.executeUpdate();
            prep.close();
        } catch (SQLException ex) {
            Logger.getLogger(JDBCMedicalRecordManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Retrieves a List<MedicalRecord> which provides all the medical's record in a whole.
     * Create a List<MedicalRecord> obtaining the id and date of each one from database.
     *
     * @param patient_id id of patient, related to the medical records that it has.
     * @throws SQLException if there is an error during the SQL operation.
     */
    public List<MedicalRecord> findByPatientId (int patient_id) {
        List<MedicalRecord> records = new ArrayList<>();
        try {
            String sql = "SELECT id, date FROM MedicalRecord WHERE patient_id = ?";
            PreparedStatement prep = cM.getConnection().prepareStatement(sql);
            prep.setInt(1, patient_id);

            ResultSet rs = prep.executeQuery();
            if (rs.next()) {
                MedicalRecord record = new MedicalRecord();
                record.setId(rs.getInt("id"));
                record.setDateAsString(rs.getString("date"));
                records.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    /**
     * Retrieves a MedicalRecord object from the database based on the given medical record ID.
     * The record is fetched by matching the given medical record ID with the records stored in the database.
     *
     * @param medicalRecord_id The ID of the medical record to retrieve.
     * @return A MedicalRecord object populated with data from the database, or null if no record is found.
     * @throws SQLException If there is an error accessing the database.
     */
    public MedicalRecord getMedicalRecordByID (Integer medicalRecord_id) throws SQLException {
        MedicalRecord record = null;
        try {
            String query = "SELECT * FROM MedicalRecord WHERE id = ?";
            PreparedStatement prep = cM.getConnection().prepareStatement(query);
            prep.setInt(1, medicalRecord_id);

            try (ResultSet rs = prep.executeQuery()) {
                if (rs.next()) {
                    record = new MedicalRecord();
                    record.setId(rs.getInt("id"));
                    record.setPatientId(rs.getInt("patient_id"));
                    record.setAge(rs.getInt("age"));
                    record.setWeight(rs.getDouble("weight"));
                    record.setHeight(rs.getInt("height"));
                    //converts the text to a list
                    String symptoms = (rs.getString("symptoms"));
                    record.setSymptoms(symptomsToList(symptoms));
                    //TODO añadir emg y acc
                    record.setDateAsString(rs.getString("date"));
                } else {
                    System.out.println("No Medical Record found for ID: " + medicalRecord_id);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving MedicalRecord by ID: " + e.getMessage());
            throw e;
        }
        return record;
    }

    public static List<String> symptomsToList(String sintomasTexto) {
        if (sintomasTexto == null || sintomasTexto.isEmpty()) {
            return List.of(); // null if empty
        }
        // Divide and create the list
        return Arrays.asList(sintomasTexto.split(",\\s*"));
    }

}
