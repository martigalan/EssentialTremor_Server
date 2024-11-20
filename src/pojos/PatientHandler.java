package pojos;

import data.ACC;
import data.EMG;
import jdbc.*;
import mainServer.MainServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static pojos.Doctor.splitToIntegerList;
import static pojos.Doctor.splitToStringList;

public class PatientHandler implements Runnable{

    /**
     * Connexion socket
     */
    private static Socket socket;
    /**
     * Input control
     */
    private BufferedReader in;
    /**
     * Output control
     */
    private PrintWriter out;
    /**
     * Connexion manager
     */
    public static ConnectionManager connectionManager;
    /**
     * User manager
     */
    public static JDBCUserManager userManager;
    /**
     * Doctor manager
     */
    public static JDBCDoctorManager doctorManager;
    /**
     * DoctorsNote manager
     */
    public static JDBCDoctorNotesManager doctorNotesManager;
    /**
     * MedicalRecord manager
     */
    public static JDBCMedicalRecordManager medicalRecordManager;
    /**
     * Patient manager
     */
    public static JDBCPatientManager patientManager;
    /**
     * State manager
     */
    public static JDBCStateManager stateManager;
    /**
     * Treatment manager
     */
    public static JDBCTreatmentManager treatmentManager;

    /**
     * Constructor
     * @param clientSocket connexion socket
     * @param dbConnection connexion manager
     */
    public PatientHandler(Socket clientSocket, ConnectionManager dbConnection) {
        this.socket = clientSocket;
        this.connectionManager = dbConnection;
    }

    /**
     * Logic that is established when a Patient connects to the application.
     * This establishes the methods the server employs depending on control words that the "client" application sends to the server.
     */
    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            userManager = new JDBCUserManager(connectionManager);
            doctorManager = new JDBCDoctorManager(connectionManager);
            doctorNotesManager = new JDBCDoctorNotesManager(connectionManager);
            medicalRecordManager = new JDBCMedicalRecordManager(connectionManager);
            patientManager = new JDBCPatientManager(connectionManager);
            stateManager = new JDBCStateManager(connectionManager);
            treatmentManager = new JDBCTreatmentManager(connectionManager);

            String command;
            while ((command = in.readLine()) != null) {
                switch (command) {
                    case "register":
                        handleRegister();
                        break;
                    case "login":
                        handleLogin();
                        break;
                    case "MedicalRecord":
                        handleMedicalRecord();
                        break;
                    case "DoctorsNote":
                        handleDoctorsNote();
                        break;
                    case "exit":
                        in.close();
                        out.close();
                        socket.close();
                        return;
                    default:
                        out.println("Comando no reconocido.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            releaseResourcesPatient(in, out, socket);
        }
    }

    /**
     * Handles medical records.
     * The function receives from the patient all the data and stores the medical record into the ddbb.
     * It sends the patient a control word to tell if the process worked correctly.
     * @throws IOException in case of Input/Output error.
     * @throws SQLException in case of database error.
     */
    private void handleMedicalRecord() throws IOException, SQLException {
        MedicalRecord medicalRecord = null;
        System.out.println("Client connected. Receiving data...");

        // Read each line
        String patientName = in.readLine();
        String patientSurname = in.readLine();
        int age = Integer.parseInt(in.readLine());
        double weight = Double.parseDouble(in.readLine());
        int height = Integer.parseInt(in.readLine());
        // Symptoms
        String symptoms = in.readLine();
        List<String> listSymptoms = splitToStringList(symptoms);
        // time, acc and emg
        String time = in.readLine();
        List<Integer> listTime = splitToIntegerList(time);
        String acc = in.readLine();
        List<Integer> listAcc = splitToIntegerList(acc);
        String emg = in.readLine();
        List<Integer> listEmg = splitToIntegerList(emg);
        // genBack
        boolean geneticBackground = Boolean.parseBoolean(in.readLine());

        ACC acc1 = new ACC(listAcc, listTime);
        EMG emg1 = new EMG(listEmg, listTime);
        medicalRecord = new MedicalRecord(patientName, patientSurname, age, weight, height, listSymptoms, acc1, emg1, geneticBackground);
        if (medicalRecord != null) {
            out.println("MEDICALRECORD_SUCCESS");
            Integer patient_id = patientManager.getIdByNameSurname(patientName, patientSurname);
            medicalRecordManager.addMedicalRecord(patient_id, medicalRecord);
        } else {
            out.println("MEDICALRECORD_FAILED");
        }
    }

    /**
     * The function lets the patient choose a medical record and then a doctors note associated to it.
     * It sends the patient a control word to tell if the process worked correctly.
     * @throws SQLException
     * @throws IOException
     */
    private void handleDoctorsNote() throws SQLException, IOException {
        DoctorsNote dn =  null;

        String patientName = in.readLine();
        String patientSurname = in.readLine();

        Integer patient_id = patientManager.getIdByNameSurname(patientName, patientSurname);
        //obtain list of medical records associated to the patient
        //they only have id and date to simplify the data download from ddbb
        List<MedicalRecord> medicalRecords = medicalRecordManager.findByPatientId(patient_id);

        out.println(medicalRecords.size());
        for (MedicalRecord record : medicalRecords) {
            out.write("ID: " + record.getId() + ", Date: " + record.getDate() + "\n");
            //System.out.println(("ID: " + record.getId() + ", Date: " + record.getDate() + "\n"));
            out.flush();
        }

        //chosen medical record
        Integer mr_id = Integer.parseInt(in.readLine());
        //doctors note associated to the medical record
        List<DoctorsNote> doctorsNotes = null;
        doctorsNotes = doctorNotesManager.getDoctorsNoteByMRID(mr_id);
        if (doctorsNotes!=null) {
            String approval = "FOUND";
            out.println(approval);

            out.println(doctorsNotes.size());
            for (DoctorsNote note : doctorsNotes) {
                out.write("ID: " + note.getId() + ", Date: " + note.getDate() + "\n");
                //System.out.println(("ID: " + record.getId() + ", Date: " + record.getDate() + "\n"));
                out.flush();
            }

            Integer dn_id = Integer.parseInt(in.readLine());
            DoctorsNote doctorsNote = doctorNotesManager.getDoctorsNoteByID(dn_id);

            out.println(doctorsNote.getDoctorName());
            out.println(doctorsNote.getDoctorSurname());
            out.println(doctorsNote.getNotes());
            out.println(doctorsNote.getState());
            out.println(doctorsNote.getTreatment());
            out.println(doctorsNote.getDate());
        }else {
            String approval = "NOT_FOUND";
            out.println(approval);
        }
    }

    /**
     * Handles login.
     * The server receives data (username and password) and checks in the bbdd if the user has permission to enter as a patient.
     * Sends the patient control words to tell them if they were able or not to login.
     * @throws IOException in case of Input/Output exception.
     * @throws SQLException in case of database error.
     */
    private void handleLogin() throws IOException, SQLException {
        String loginData = in.readLine();
        String[] data = loginData.split("\\|");
        String usernamePatient = data[0];
        String encryptedPassword = data[1];

        //checks login info
        if (userManager.verifyUsername(usernamePatient, "patient") && userManager.verifyPassword(usernamePatient, encryptedPassword)) {
            out.println("LOGIN_SUCCESS");
            int user_id = userManager.getId(usernamePatient);
            Patient patient = patientManager.getPatientByUserId(user_id);
            String patientInfo = patient.getName() + "|" + patient.getSurname() + "|" + patient.getGenetic_background();
            out.println(patientInfo);
            return;
        } else {
            out.println("LOGIN_FAILED");
            return;
        }
    }

    /**
     * Handles registration.
     * In this method, the server receives data (name, surname, username and password), to add the patient to the database and create a user for future login.
     * The server sends the patient a control word to tell them if they were able to register or not.
     * @throws IOException in case of Input/Output exception.
     */
    private void handleRegister() throws IOException {
        String data = in.readLine();
        //add user information to database
        Patient patient = processRegisterInfo(data);
        //get userId to add patient to database
        String username = findUsername(data);
        int userId = userManager.getId(username);
        if (patient != null) {
            patientManager.addPatient(patient, userId);
            out.println("REGISTER_SUCCESS");
        } else {
            out.println("REGISTER_FAILED");
        }
    }
    /**
     * Auxiliary function to find the username in the data received by the server.
     * It divides the data and gets the username parameter.
     * @param patientData string containing all the data.
     * @return the username found in the data.
     */
    public static String findUsername (String patientData){
        String[] data = patientData.split("\\|");
        if (data.length == 6) {
            String name = data[0];
            String surname = data[1];
            boolean geneticBackground = Boolean.parseBoolean(data[2]);
            String username = data[3];
            String encryptedPassword = data[4];
            String role = data[5];
            
            return username;
        } else {
            System.out.println("Error: incorrect patient data format.");
            return null;
        }
    }

    /**
     * Function to create the doctor and add to the User table.
     * @param patientData String with the doctor data.
     * @return a doctor created with the received data.
     */
    public static Patient processRegisterInfo(String patientData) {
        //Client info comes as: "name|surname|genetic_background|username|password"
        String[] data = patientData.split("\\|");
        if (data.length == 6) {
            String name = data[0];
            String surname = data[1];
            boolean geneticBackground = Boolean.parseBoolean(data[2]);
            String username = data[3];
            String encryptedPassword = data[4];
            String role = data[5];

            //from hexadecimal (String) t byte[]
            byte[] passwordBytes = hexStringToByteArray(encryptedPassword);
            User user = new User(username, passwordBytes, role);
            //TODO si es necesario meter User
            //add register info (username and password) to ddbb
            userManager.addUser(user);
            Patient patient = new Patient(name, surname, geneticBackground);
            System.out.println("Patient added successfully: " + patient.getName() + " " + patient.getSurname());
            return patient;
        } else {
            System.out.println("Error: incorrect patient data format.");
            return null;
        }
    }

    /**
     * Transforms a hexadecimal (String) to byte.
     * @param hex String value
     * @return byte value
     */
    public static byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }

    /**
     * Closes all the resources used.
     * @param bufferedReader input control.
     * @param printWriter output control
     * @param socket connexion control.
     */
    private static void releaseResourcesPatient(BufferedReader bufferedReader, PrintWriter printWriter, Socket socket) {
        try {
            bufferedReader.close();
        } catch (IOException ex) {
            Logger.getLogger(PatientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            printWriter.close();
        } catch (Exception ex) {
            Logger.getLogger(PatientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(PatientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
