package pojos;

import data.ACC;
import data.EMG;
import jdbc.*;
import mainServer.MainServer;
import security.Decryptor;
import security.KeyGeneration;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static pojos.Doctor.splitToIntegerList;
import static pojos.Doctor.splitToStringList;

public class PatientHandler implements Runnable {

    /**
     * Connexion socket
     */
    private Socket socket;
    /**
     * Input control
     */
    private static BufferedReader in;
    /**
     * Output control
     */
    private static PrintWriter out;
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
     * Private key used to decrypt
     */
    public static PrivateKey privateKey;

    /**
     * Constructor
     *
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

            //Obtengo ambas claves
            KeyPair keyPair = KeyGeneration.generateKeys();
            //Envio la clave pública al cliente
            out.println(KeyGeneration.getPublicKeyAsString(keyPair));
            //Guardar la clave privada
            privateKey = keyPair.getPrivate();

            while (!Thread.currentThread().isInterrupted() && socket.isConnected()) {
                if ((command = in.readLine()) != null) {
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
                            System.out.println("Patient socket closed.");
                            return;
                        default:
                            out.println("Unrecognized command.");
                    }
                }
            }
        } catch (SocketException e) {
            System.out.println("Patient Socket closed: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error in client connection: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            releaseResourcesPatient(in, out, socket);
            System.out.println("Thread terminated for patient.");
        }
    }


    /**
     * Handles medical records.
     * The function receives from the patient all the data and stores the medical record into the ddbb.
     * It sends the patient a control word to tell if the process worked correctly.
     *
     * @throws IOException  in case of Input/Output error.
     * @throws SQLException in case of database error.
     */
    private void handleMedicalRecord() throws Exception {
        MedicalRecord medicalRecord = null;
        System.out.println("Client connected. Receiving data...");

        // Read each line
        String patientName = in.readLine();
        String decryptedPatientName = Decryptor.decryptData(patientName, privateKey);
        String patientSurname = in.readLine();
        String decryptedPatientSurname = Decryptor.decryptData(patientSurname, privateKey);
        //int age = Integer.parseInt(in.readLine());
        String encryptedAge = in.readLine();
        String decryptedAge = Decryptor.decryptData(encryptedAge, privateKey);
        int age = Integer.valueOf(decryptedAge);
        //double weight = Double.parseDouble(in.readLine());
        String encryptedWeight = in.readLine();
        String decryptedWeight = Decryptor.decryptData(encryptedWeight, privateKey);
        double weight = Double.parseDouble(decryptedWeight);
        //int height = Integer.parseInt(in.readLine());
        String encryptedHeight = in.readLine();
        String decryptedHeight = Decryptor.decryptData(encryptedHeight, privateKey);
        int height = Integer.valueOf(decryptedHeight);

        // Symptoms
        String symptoms = in.readLine();
        String decryptedSymptoms = Decryptor.decryptData(symptoms, privateKey);
        List<String> listSymptoms = splitToStringList(decryptedSymptoms);
        // time, acc and emg
        String time = in.readLine();
        //String decryptedTime = Decryptor.decryptData(time, privateKey);
        List<Integer> listTime = splitToIntegerList(time);
        String acc = in.readLine();
        //String decryptedAcc = Decryptor.decryptData(acc, privateKey);
        List<Integer> listAcc = splitToIntegerList(acc);
        String emg = in.readLine();
        //String decryptedEmg = Decryptor.decryptData(emg, privateKey);
        List<Integer> listEmg = splitToIntegerList(emg);
        // genBack
        //boolean geneticBackground = Boolean.parseBoolean(in.readLine());
        String encryptedGB = in.readLine();
        String decryptedGB = Decryptor.decryptData(encryptedGB, privateKey);
        boolean geneticBackground = Boolean.parseBoolean(decryptedGB);

        ACC acc1 = new ACC(listAcc, listTime);
        EMG emg1 = new EMG(listEmg, listTime);
        medicalRecord = new MedicalRecord(decryptedPatientName, decryptedPatientSurname, age, weight, height, listSymptoms, acc1, emg1, geneticBackground);
        if (medicalRecord != null) {
            out.println("MEDICALRECORD_SUCCESS");
            Integer patient_id = patientManager.getIdByNameSurname(decryptedPatientName, decryptedPatientSurname);
            medicalRecordManager.addMedicalRecord(patient_id, medicalRecord);
        } else {
            out.println("MEDICALRECORD_FAILED");
        }
    }

    /**
     * The function lets the patient choose a medical record and then a doctors note associated to it.
     * It sends the patient a control word to tell if the process worked correctly.
     *
     * @throws SQLException
     * @throws IOException
     */
    private void handleDoctorsNote() throws Exception {
        DoctorsNote dn = null;

        String patientName = in.readLine();
        String decryptedPatientName = Decryptor.decryptData(patientName, privateKey);
        String patientSurname = in.readLine();
        String decryptedPatientSurname = Decryptor.decryptData(patientSurname, privateKey);

        Integer patient_id = patientManager.getIdByNameSurname(decryptedPatientName, decryptedPatientSurname);
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
        //Integer mr_id = Integer.parseInt(in.readLine());
        String encryptedMR_ID = in.readLine();
        String decryptedMR_ID = Decryptor.decryptData(encryptedMR_ID, privateKey);
        Integer mr_id = Integer.parseInt(decryptedMR_ID);
        //check if the mr is correct
        String mrCorrect;
        MedicalRecord mr = medicalRecordManager.getMedicalRecordByID(mr_id);
        String mrNull;
        if (mr == null){
            mrNull = "NULL";
            out.println(mrNull);
        } else {
            mrNull = "NOT_NULL";
            out.println(mrNull);

            String mrPName = patientManager.getPatientById(mr.getPatientId()).getName();
            String mrPSurname = patientManager.getPatientById(mr.getPatientId()).getSurname();
            if (!mrPName.equals(decryptedPatientName) && !mrPSurname.equals(decryptedPatientSurname)) {
                mrCorrect = "NOT_CORRECT";
                out.println(mrCorrect);
                out.flush();
            } else {
                mrCorrect = "CORRECT";
                out.println(mrCorrect);
                //doctors note associated to the medical record
                List<DoctorsNote> doctorsNotes = null;
                doctorsNotes = doctorNotesManager.getDoctorsNoteByMRID(mr_id);
                if (doctorsNotes != null) {
                    String approval = "FOUND";
                    out.println(approval);

                    out.println(doctorsNotes.size());
                    for (DoctorsNote note : doctorsNotes) {
                        out.write("ID: " + note.getId() + ", Date: " + note.getDate() + "\n");
                        //System.out.println(("ID: " + record.getId() + ", Date: " + record.getDate() + "\n"));
                        out.flush();
                    }

                    //Integer dn_id = Integer.parseInt(in.readLine());
                    String encryptedDN_ID = in.readLine();
                    String decryptedDN_ID = Decryptor.decryptData(encryptedDN_ID, privateKey);
                    Integer dn_id = Integer.valueOf(decryptedDN_ID);
                    DoctorsNote doctorsNote = doctorNotesManager.getDoctorsNoteByID(dn_id);

                    String dnNull;
                    if (doctorsNote == null) {
                        dnNull = "NULL";
                        out.println(dnNull);
                    } else {
                        dnNull = "NOT_NULL";
                        out.println(dnNull);
                        Doctor doctor = doctorManager.getDoctorById(doctorsNote.getDoctorId());

                        //check if dn if correct
                        Integer dnMR = doctorsNote.getMedicalRecordId();
                        String dnCorrect;
                        if (dnMR != mr_id) {
                            dnCorrect = "NOT_CORRECT";
                            out.println(dnCorrect);
                        } else {
                            dnCorrect = "CORRECT";
                            out.println(dnCorrect);

                            out.println(doctor.getName());
                            out.println(doctor.getSurname());
                            out.println(doctorsNote.getNotes());
                            out.println(doctorsNote.getState().getId());
                            out.println(doctorsNote.getTreatment().getId());
                            out.println(doctorsNote.getDate());
                        }
                    }
                } else {
                    String approval = "NOT_FOUND";
                    out.println(approval);
                }
            }
        }
    }

    /**
     * Handles login.
     * The server receives data (username and password) and checks in the bbdd if the user has permission to enter as a patient.
     * Sends the patient control words to tell them if they were able or not to login.
     *
     * @throws IOException  in case of Input/Output exception.
     * @throws SQLException in case of database error.
     */
    private void handleLogin() throws IOException, SQLException {
        String loginData = in.readLine();
        try {
            String decryptedLoginData = Decryptor.decryptData(loginData, privateKey);
            String[] data = decryptedLoginData.split("\\|");
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles registration.
     * In this method, the server receives data (name, surname, username and password), to add the patient to the database and create a user for future login.
     * The server sends the patient a control word to tell them if they were able to register or not.
     *
     * @throws IOException in case of Input/Output exception.
     */
    private void handleRegister() throws IOException {
        String registerData = in.readLine();
        try {
            String decryptedRegisterData = Decryptor.decryptData(registerData, privateKey);
            //add user information to database
            Patient patient = processRegisterInfo(decryptedRegisterData);
            //get userId to add patient to database
            String username = findUsername(decryptedRegisterData);
            int userId = userManager.getId(username);
            if (patient != null) {
                patientManager.addPatient(patient, userId);
                out.println("REGISTER_SUCCESS");
            } else {
                out.println("REGISTER_FAILED");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Auxiliary function to find the username in the data received by the server.
     * It divides the data and gets the username parameter.
     *
     * @param patientData string containing all the data.
     * @return the username found in the data.
     */
    public static String findUsername(String patientData) {
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
     *
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
     *
     * @param hex String value
     * @return byte value
     */
    public static byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * Closes all the resources used.
     *
     * @param bufferedReader input control.
     * @param printWriter    output control
     * @param socket         connexion control.
     */
    private static void releaseResourcesPatient(BufferedReader bufferedReader, PrintWriter printWriter, Socket socket) {
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (printWriter != null) printWriter.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            Logger.getLogger(PatientHandler.class.getName()).log(Level.SEVERE, "Error closing resources", e);
        }
    }
}
