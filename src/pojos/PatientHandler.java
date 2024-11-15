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

    private static Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public static ConnectionManager connectionManager;
    public static JDBCUserManager userManager;
    public static JDBCDoctorManager doctorManager;
    public static JDBCDoctorNotesManager doctorNotesManager;
    public static JDBCMedicalRecordManager medicalRecordManager;
    public static JDBCPatientManager patientManager;
    public static JDBCStateManager stateManager;
    public static JDBCTreatmentManager treatmentManager;

    public PatientHandler(Socket clientSocket, ConnectionManager dbConnection) {
        this.socket = clientSocket;
        this.connectionManager = dbConnection;
    }
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
        //todo possibly change to get all the dn associated to that mr
        DoctorsNote doctorsNote = null;
        doctorsNote = doctorNotesManager.getDoctorsNoteByID(mr_id);
        if (doctorsNote!=null) {
            String approval = "FOUND";
            out.println(approval);
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

    public static byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }

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
