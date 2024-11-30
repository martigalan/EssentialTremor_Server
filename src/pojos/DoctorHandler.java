package pojos;

import jdbc.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.time.ZonedDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

import static pojos.Patient.joinIntegersWithCommas;
import static pojos.Patient.joinWithCommas;

public class DoctorHandler implements Runnable {

    /**
     * Connexion socket
     */
    private Socket socket;
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
     * Patient-Doctor relationship manager
     */
    public static JDBCHasPatientManager hasPatientManager;
    /**
     * Patient-MedicalRecord manager
     */
    public static JDBCHasMedicalRecordManager hasMedicalRecordManager;

    /**
     * Constructor
     *
     * @param clientSocket connexion socket
     * @param dbConnection connexion manager
     */
    public DoctorHandler(Socket clientSocket, ConnectionManager dbConnection) {
        this.socket = clientSocket;
        this.connectionManager = dbConnection;
    }

    /**
     * Logic that is established when a Doctor connects to the application.
     * This establishes the methods the server employs depending on control words that the "doctor" application sends to the server.
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
            hasPatientManager = new JDBCHasPatientManager(connectionManager);
            hasMedicalRecordManager = new JDBCHasMedicalRecordManager(connectionManager);
            String command;

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
                            System.out.println("Doctor socket closed.");
                            return;
                        default:
                            out.println("Unrecognized command.");
                    }
                }
            }
        } catch (SocketException e) {
            System.out.println("Doctor socket closed: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error in client connection: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        } finally {
            releaseResourcesDoctor(in, out, socket);
            System.out.println("Thread terminated for doctor.");
        }
    }

    /**
     * Handles login.
     * The server receives data (username and password) and checks in the bbdd if the user has permission to enter as a doctor.
     * Sends the doctor control words to tell them if they were able or not to login.
     *
     * @throws IOException  in case of Input/Output exception.
     * @throws SQLException in case of database error.
     */
    private void handleLogin() throws IOException, SQLException {
        String loginData = in.readLine();
        String[] data = loginData.split("\\|");
        String usernameDoctor = data[0];
        String encryptedPassword = data[1];
        //checks login info
        if (userManager.verifyUsername(usernameDoctor, "doctor") && userManager.verifyPassword(usernameDoctor, encryptedPassword)) {
            out.println("LOGIN_SUCCESS");
            int user_id = userManager.getId(usernameDoctor);
            Doctor doctor = doctorManager.getDoctorByUserId(user_id);
            String doctorInfo = doctor.getName() + "|" + doctor.getSurname();
            out.println(doctorInfo);
        } else {
            out.println("LOGIN_FAILED");
        }
    }

    /**
     * Handles registration.
     * In this method, the server receives data (name, surname, username and password), to add the doctor to the database and create a user for future login.
     * The server sends the doctor a control word to tell them if they were able to register or not.
     *
     * @throws IOException in case of Input/Output exception.
     */
    private void handleRegister() throws IOException {
        String data = in.readLine();
        //add user information to database
        Doctor doctor = processRegisterInfo(data);
        //get userId to add patient to database
        String username = findUsername(data);
        int userId = userManager.getId(username);
        if (doctor != null) {
            doctorManager.addDoctor(doctor, userId);
            out.println("REGISTER_SUCCESS");
        } else {
            out.println("REGISTER_FAILED");
        }
    }

    /**
     * Auxiliary function to find the username in the data received by the server.
     * It divides the data and gets the username parameter.
     *
     * @param doctorData string containing all the data.
     * @return the username found in the data.
     */
    public static String findUsername(String doctorData) {
        String[] data = doctorData.split("\\|");
        if (data.length == 5) {
            String name = data[0];
            String surname = data[1];
            String username = data[2];
            String encryptedPassword = data[3];
            String role = data[4];

            return username;
        } else {
            System.out.println("Error: incorrect doctor data format.");
            return null;
        }
    }

    /**
     * Function to create the doctor and add to the User table.
     *
     * @param doctorData String with the doctor data.
     * @return a doctor created with the received data.
     */
    public static Doctor processRegisterInfo(String doctorData) {
        //Data comes in this format: "name|surname|username|password"
        String[] data = doctorData.split("\\|");
        if (data.length == 5) {
            String name = data[0];
            String surname = data[1];
            String username = data[2];
            String encryptedPassword = data[3];
            String role = data[4];

            //hexadecimal (String) to byte[]
            byte[] passwordBytes = hexStringToByteArray(encryptedPassword);
            User user = new User(username, passwordBytes, role);
            userManager.addUser(user);
            Doctor doctor = new Doctor(name, surname);
            System.out.println("Doctor added successfully: " + doctor.getName() + " " + doctor.getSurname());
            return doctor;
        } else {
            System.out.println("Error: incorrect doctor data format.");
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
     * Handles medical record.
     * The method lets the doctor choose a patient and then choose a medical record to see.
     * This method also adds the necessary info into the ddbb, such as the Patient-Doctor connexion.
     * It sends the doctor a confirmation if the process worked correctly.
     *
     * @throws IOException  in case of Input/Ouput error.
     * @throws SQLException in case of database error.
     */
    private void handleMedicalRecord() throws IOException, SQLException {
        MedicalRecord medicalRecord = null;
        Patient patient = null;
        //find doctor_id for later use
        String doctorName = in.readLine();
        String doctorSurname = in.readLine();
        Integer doctor_id = doctorManager.getIdByNameSurname(doctorName, doctorSurname);

        //get all the patient ids, names and surnames and send
        List<Patient> pList = patientManager.getPatients();
        Integer numberOfPatients = pList.size();
        out.println(numberOfPatients);
        for (Patient p : pList) {
            out.write("ID: " + p.getId() + ", Name: " + p.getName() + ", Surname: " + p.getSurname() + "\n");
            out.flush();
        }
        //receive the desired patient id and add to HasPatient table if its not already there
        Integer patient_id = Integer.parseInt(in.readLine());
        //check if its already in the table
        Patient p = patientManager.getPatientById(patient_id);
        if (p != null) {
            String message = "NOT_NULL";
            out.println(message);
            Boolean check = hasPatientManager.isAlreadyCreated(doctor_id, patient_id);
            if (!check) {
                hasPatientManager.addPatientDoctor(doctor_id, patient_id);
            }

            //go to the medical records and choose those that have the patient_id
            //they only have id and date to simplify the data download from ddbb
            List<MedicalRecord> medicalRecords = medicalRecordManager.findByPatientId(patient_id);
            Integer numberOfMR = medicalRecords.size();

            if (numberOfMR == 0) {
                out.println("NOT_FOUND");
            } else if (numberOfMR > 0) {
                out.println("FOUND");
                out.println(numberOfMR);
                for (MedicalRecord record : medicalRecords) {
                    out.write("ID: " + record.getId() + ", Date: " + record.getDate() + "\n");
                    out.flush();
                }
                Integer mr_id = Integer.parseInt(in.readLine());

                //check if it exists
                MedicalRecord mr = medicalRecordManager.getMedicalRecordByID(mr_id);
                if (mr == null){
                    String mrNull = "NULL";
                    out.println(mrNull);
                } else {
                    String mrNull = "NOT_NULL";
                    out.println(mrNull);

                    //check if the mr is form that patient
                    if (mr.getPatientId() != patient_id){
                        String mrCorrect = "NOT_CORRECT";
                        out.println(mrCorrect);
                    } else {
                        String mrCorrect = "CORRECT";
                        out.println(mrCorrect);

                        //once I have my medicalRecord, add it to my doctor, to associate it in ddbb
                        hasMedicalRecordManager.addMedicalRecordDoctor(doctor_id, mr_id);
                        //obtain this medicalRecord from ddbb to send it to the Doctor
                        medicalRecord = medicalRecordManager.getMedicalRecordByID(mr_id);
                        //obtain data of patient by id
                        patient = patientManager.getPatientById(patient_id);
                        if (medicalRecord != null) {
                            out.println("SEND_MEDICALRECORD");
                            //send data
                            out.println(patient.getName());
                            out.println(patient.getSurname());
                            out.println(patient.getGenetic_background());
                            out.println(medicalRecord.getAge());
                            out.println(medicalRecord.getWeight());
                            out.println(medicalRecord.getHeight());
                            //symptoms
                            String symptoms = joinWithCommas(medicalRecord.getSymptoms());
                            out.println(symptoms);
                            //timestamp
                            String time = joinIntegersWithCommas(medicalRecord.getAcceleration().getTimestamp());
                            out.println(time);
                            //acc
                            String acc = joinIntegersWithCommas(medicalRecord.getAcceleration().getSignalData());
                            out.println(acc);
                            //emg
                            String emg = joinIntegersWithCommas(medicalRecord.getEmg().getSignalData());
                            out.println(emg);
                            //Receives approval
                            String approval = in.readLine();
                            if (approval.equals("MEDICALRECORD_SUCCESS")) {
                                System.out.println("Medical Record sent correctly");
                            } else {
                                System.out.println("Couldn't send Medical Record. Please try again.");
                            }
                        } else {
                            out.println("ERROR");
                        }
                    }
                }
            }
        } else {
            String message = "NULL";
            out.println(message);
        }
    }

    /**
     * Handles doctor notes.
     * This method receives the doctors note data from the doctor and stores it in the ddbb.
     * It sends the doctor a confirmation if the process worked correctly.
     *
     * @throws IOException  in case of Input/Output error.
     * @throws SQLException in case of database error.
     */
    private void handleDoctorsNote() throws IOException, SQLException {
        //receive doctors note
        String dName = in.readLine();
        String dSurname = in.readLine();
        String notes = in.readLine();
        Integer st_id = Integer.parseInt(in.readLine());
        State st = State.getById(st_id);
        Integer trt_id = Integer.parseInt(in.readLine());
        Treatment trt = Treatment.getById(trt_id);
        String dateTxt = in.readLine();
        LocalDate date = Date.valueOf(dateTxt).toLocalDate();
        Integer mr_id = Integer.valueOf(in.readLine());

        DoctorsNote dn = new DoctorsNote(dName, dSurname, notes, st, trt, date);
        dn.setMedicalRecordId(mr_id);

        Integer doctor_id = doctorManager.getIdByNameSurname(dName, dSurname);
        dn.setDoctorId(doctor_id);

        if (dn != null) {
            String approval = "DOCTORNOTE_SUCCESS";
            out.println(approval);
            doctorNotesManager.addDoctorNote(dn);
        } else {
            String approval = "DOCTORNOTE_FAILED";
            out.println(approval);
        }
    }

    /**
     * Closes all the resources used.
     *
     * @param bufferedReader input control.
     * @param printWriter    output control
     * @param socket         connexion control.
     */
    private static void releaseResourcesDoctor(BufferedReader bufferedReader, PrintWriter printWriter, Socket socket) {
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (printWriter != null) printWriter.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            Logger.getLogger(PatientHandler.class.getName()).log(Level.SEVERE, "Error closing resources", e);
        }
    }
}
