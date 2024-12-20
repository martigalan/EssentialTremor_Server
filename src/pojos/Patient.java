package pojos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Patient {

    /**
     * Patients name
     */
    private String name;
    /**
     * Patients surname
     */
    private String surname;
    /**
     * Boolean to identify if the patient has a genetic predisposition of essential tremor
     * TRUE if there is, FALSE if not
     */
    private Boolean genetic_background;
    /**
     * User to store username and password for the application
     */
    private User user;
    /**
     * A list of all the medical records the patient has
     */
    private List<MedicalRecord> medicalRecords;
    /**
     * A list of the doctors that the patient has
     */
    private List<Doctor> doctors;
    /**
     * ID of the patient in the database
     */
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Empty constructor
     */
    public Patient() {
    }

    /**
     * Constructor
     *
     * @param name    patients name
     * @param surname patients surname
     * @param genBack patient genetic background of essential tremor
     */
    public Patient(String name, String surname, Boolean genBack) {
        this.name = name;
        this.surname = surname;
        this.genetic_background = genBack;
        this.medicalRecords = new ArrayList<MedicalRecord>();
        this.doctors = new ArrayList<Doctor>();
    }

    public void setMedicalRecords(List<MedicalRecord> medicalRecords) {
        this.medicalRecords = medicalRecords;
    }

    public List<Doctor> getDoctors() {
        return doctors;
    }

    public void setDoctors(List<Doctor> doctors) {
        this.doctors = doctors;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<MedicalRecord> getMedicalRecords() {
        return medicalRecords;
    }

    public Boolean getGenetic_background() {
        return genetic_background;
    }

    public void setGenetic_background(Boolean genetic_background) {
        this.genetic_background = genetic_background;
    }

    public void setMedicalRecord(List<MedicalRecord> medicalRecords) {
        this.medicalRecords = medicalRecords;
    }

    /**
     * Patients String representation
     * @return String representation
     */
    @Override
    public String toString() {
        return "- Name: " + name + '\'' +
                "- Surname: " + surname + '\'';
        //"- State: " + state +
        //"- Treatment: " + treatment;
    }

    /**
     * Creates a medical record calling other auxiliar functions
     */
    private void openRecord(){
        MedicalRecord record = askData();
        record.setPatientName(this.name);
        record.setPatientSurname(this.surname);
        record.setGenetic_background(this.genetic_background);
        //Data data = obtainData();
        //record.setAcceleration(data.getAcc());
        //record.setEmg(data.getEmg());
        this.getMedicalRecords().add(record);
    }

    /**
     * Asks the user to input additional data for the medical record: age, weight, height and symptoms.
     *
     * @return a partially-complete Medical Record
     */
    private MedicalRecord askData() {
        Scanner sc = new Scanner(System.in);
        System.out.println("- Age: ");
        int age = sc.nextInt();
        System.out.println("- Weight (kg): ");
        double weight = sc.nextDouble();
        System.out.println("- Height (cm): ");
        int height = sc.nextInt();
        System.out.println("- Symptoms (enter symptoms separated by commas): ");
        String symptomsInput = sc.nextLine();

        //Takes the symptoms input and creates a List
        List<String> symptoms = Arrays.asList(symptomsInput.split(","));
        symptoms = symptoms.stream().map(String::trim).collect(Collectors.toList()); // Trim extra spaces
        sc.close();
        return new MedicalRecord(age, weight, height, symptoms);
    }

    /**
     * Chooses the medical record to send
     *
     * @return the Medical Record selected by the user
     */
    public MedicalRecord chooseMR() {
        int size = this.getMedicalRecords().size();
        if (size == 0) {
            System.out.println("No medical records found.");
            return null;
        }

        System.out.println("You have the following medical records available:");
        for (int i = 0; i < size; i++) {
            System.out.println((i + 1) + ". " + this.getMedicalRecords().get(i).toString());
        }

        Scanner sc = new Scanner(System.in);
        int choice = -1;
        while (choice < 1 || choice > size) {
            System.out.print("Enter the number of the medical record you want to select: ");
            if (sc.hasNextInt()) {
                choice = sc.nextInt();
            } else {
                System.out.println("Invalid input. Please enter a valid number.");
                sc.next();
            }
        }

        MedicalRecord mr = this.getMedicalRecords().get(choice - 1);
        return mr;
    }

    /**
     * Send the Medical Record to the server for the doctor to see
     *
     * @param medicalRecord complete Medical Record
     * @param socket        Socket with the connection to the server
     * @throws IOException in case the connection fails
     */
    public void sendMedicalRecord(MedicalRecord medicalRecord, Socket socket) throws IOException {
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
        System.out.println("Connection established... sending text");
        printWriter.println(medicalRecord.getPatientName());
        printWriter.println(medicalRecord.getPatientSurname());
        printWriter.println(medicalRecord.getAge());//int
        printWriter.println(medicalRecord.getWeight());//double
        printWriter.println(medicalRecord.getHeight());//int
        //symptoms
        String symptoms = joinWithCommas(medicalRecord.getSymptoms());
        System.out.println(symptoms);
        //timestamp
        String time = joinIntegersWithCommas(medicalRecord.getAcceleration().getTimestamp());
        printWriter.println(time);
        //acc
        String acc = joinIntegersWithCommas(medicalRecord.getAcceleration().getSignalData());
        printWriter.println(acc);
        //emg
        String emg = joinIntegersWithCommas(medicalRecord.getEmg().getSignalData());
        printWriter.println(emg);
        printWriter.println(medicalRecord.getGenetic_background());//boolean
        //releaseSendingResources(printWriter, socket);
    }

    /**
     * Creates a String from a List
     *
     * @param list list of Strings
     * @return String with items of the list separated with commas
     */
    public static String joinWithCommas(List<String> list) {
        return String.join(",", list);
    }

    /**
     * Creates a String with the integer values of a List
     *
     * @param list list of Integers
     * @return String with the integer values separated with commas
     */
    public static String joinIntegersWithCommas(List<Integer> list) {
        return list.stream()
                .map(String::valueOf) // Convert Integer to String
                .collect(Collectors.joining(","));
    }

    /**
     * Gets the doctors note about the medical record that was previously sent
     *
     * @return DoctorsNote containing the evaluation
     * @throws IOException in case connection fails
     */
    private DoctorsNote receiveDoctorsNote()throws IOException {
        //check this one
        DoctorsNote doctorsNote = null;
        try (ServerSocket serverSocket = new ServerSocket(9009)) {  // Puerto 9009 para coincidir con el cliente
            System.out.println("Server started, waiting for client...");

            try (Socket socket = serverSocket.accept();
                 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                System.out.println("Client connected. Receiving data...");

                // Read each line
                String doctorName = bufferedReader.readLine();
                System.out.println(doctorName);
                String doctorSurname = bufferedReader.readLine();
                System.out.println(doctorSurname);
                String notes = bufferedReader.readLine();
                System.out.println(notes);
                State st = State.valueOf(bufferedReader.readLine());
                System.out.println(st);
                Treatment trt = Treatment.valueOf(bufferedReader.readLine());
                System.out.println(trt);

                releaseReceivingResources(bufferedReader, socket, serverSocket);

                doctorsNote = new DoctorsNote(doctorName, doctorSurname, notes);
                //meter esto en lista doctor
                //this is in the main
                //DoctorsNote doctorsNote = createDoctorsNote(medicalRecord);
                //medicalRecord.getDoctorsNotes().add(doctorsNote);
                return doctorsNote;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doctorsNote;
    }

    /**
     * Realeases the resources that were used
     *
     * @param bufferedReader used to read
     * @param socket         connection with the server
     * @param socketServidor server socket
     */
    private static void releaseReceivingResources(BufferedReader bufferedReader,
                                                  Socket socket, ServerSocket socketServidor) {
        try {
            bufferedReader.close();
        } catch (IOException ex) {
            Logger.getLogger(Doctor.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Doctor.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            socketServidor.close();
        } catch (IOException ex) {
            Logger.getLogger(Doctor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Displays the DoctorsNote sent by the doctor
     */
    private void seeDoctorsNotes() {
        //here the patient chooses what record they want to see

    }
}
