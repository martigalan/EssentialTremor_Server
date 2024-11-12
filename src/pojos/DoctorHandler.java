package pojos;

import jdbc.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

import static pojos.Patient.joinIntegersWithCommas;
import static pojos.Patient.joinWithCommas;

public class DoctorHandler implements Runnable {

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
    public static JDBCHasPatientManager hasPatientManager;

    public DoctorHandler(Socket clientSocket, ConnectionManager dbConnection) {
        this.socket = clientSocket;
        this.connectionManager = dbConnection;
    }

    @Override
    public void run() {
        try {
            //TODO quitar algunas cosas de estas!?
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
        }
    }

    private void handleLogin() throws IOException, SQLException {
        String loginData = in.readLine();
        String[] data = loginData.split("\\|");
        String usernameDoctor = data[0];
        String encryptedPassword = data[1];
        //checks login info
        if (userManager.verifyUsername(usernameDoctor) && userManager.verifyPassword(usernameDoctor, encryptedPassword)) {
            out.println("LOGIN_SUCCESS");
            int user_id = userManager.getId(usernameDoctor);
            Doctor doctor = doctorManager.getDoctorByUserId(user_id);
            String doctorInfo = doctor.getName() + "|" + doctor.getSurname();
            out.println(doctorInfo);
        } else {
            out.println("LOGIN_FAILED");
        }
    }

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

    public static String findUsername (String doctorData){
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
    public static Doctor processRegisterInfo(String doctorData) {
        //Los datos del cliente llegan en formato: "name|surname|username|password"
        String[] data = doctorData.split("\\|");
        if (data.length == 5) {
            String name = data[0];
            String surname = data[1];
            String username = data[2];
            String encryptedPassword = data[3];
            String role = data[4];

            //de hexadecimal (String) a byte[]
            byte[] passwordBytes = hexStringToByteArray(encryptedPassword);
            User user = new User(username, passwordBytes, role);
            //TODO si es necesario meter User
            userManager.addUser(user);
            Doctor doctor = new Doctor(name, surname);
            System.out.println("Doctor added successfully: " + doctor.getName() + " " + doctor.getSurname());
            return doctor;
        } else {
            System.out.println("Error: incorrect doctor data format.");
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

    private void handleMedicalRecord() throws IOException, SQLException {
        MedicalRecord medicalRecord = null;

        //find doctor_id for later use
        String doctorName = in.readLine();
        String doctorSurname = in.readLine();
        Integer doctor_id = doctorManager.getIdByNameSurname(doctorName, doctorSurname);

        //get all the patient ids, names and surnames and send
        List<Patient> pList = patientManager.getPatients();
        for (Patient p : pList) {
            out.write("ID: " + p.getId() + ", Name: " + p.getName() + ", Surname: " + p.getSurname()+ "\n");
        }
        //receive the desired patient id and add to HasPatient table
        Integer patient_id = Integer.parseInt(in.readLine());
        hasPatientManager.addPatientDoctor(doctor_id, patient_id);

        //go to the medical records and choose those that have the pateint_id
        //they only have id and date to simplify the data download from ddbb
        List<MedicalRecord> medicalRecords = medicalRecordManager.findByPatientId(patient_id);
        for (MedicalRecord record : medicalRecords) {
            out.write("ID: " + record.getId() + ", Date: " + record.getDate() + "\n");
        }
        Integer mr_id = Integer.parseInt(in.readLine());

        medicalRecord = medicalRecordManager.getMedicalRecordByID(mr_id);
        if (medicalRecord != null) {
            out.println("SEND_MEDICALRECORD");
            //send data
            out.println(medicalRecord.getPatientName());
            out.println(medicalRecord.getPatientSurname());
            out.println(medicalRecord.getGenetic_background());
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
            out.println(medicalRecord.getGenetic_background());//boolean
            //Receives approval
            String approval = in.readLine();
            if (approval.equals("MEDICALRECORD_SUCCESS")){
                System.out.println("Medical Record sent correctly");
                return;
            } else{
                System.out.println("Couldn't send Medical Record. Please try again.");
            }
        } else {
            out.println("No medical record found for this patient.");
        }
    }

    private void handleDoctorsNote() throws IOException, SQLException {

    }
}
