import jdbc.*;
import pojos.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static ConnectionManager connectionManager;
    public static JDBCUserManager userManager;
    public static JDBCDoctorManager doctorManager;
    public static JDBCDoctorNotes doctorNotesManager;
    public static JDBCMedicalRecordManager medicalRecordManager;
    public static JDBCPatientManager patientManager;
    public static JDBCStateManager stateManager;
    public static JDBCTreatmentManager treatmentManager;
    private static boolean control;
    private static Scanner sc = new Scanner(System.in);
    private static Doctor doctor;
    private static Socket socket;
    private static PrintWriter printWriter;
    private static BufferedReader bufferedReader;

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        printWriter = null;
        bufferedReader = null;
        boolean conexion= true;

        try {
            connectionManager = new ConnectionManager();
            userManager = new JDBCUserManager(connectionManager);
            doctorManager = new JDBCDoctorManager(connectionManager);
            doctorNotesManager = new JDBCDoctorNotes(connectionManager);
            medicalRecordManager = new JDBCMedicalRecordManager(connectionManager);
            patientManager = new JDBCPatientManager(connectionManager);
            stateManager = new JDBCStateManager(connectionManager);
            treatmentManager = new JDBCTreatmentManager(connectionManager);

            //Create socket
            serverSocket = new ServerSocket(9000);

            //TODO wait for connections
            try {
                while (conexion) {
                    //This executes when we have a client
                    System.out.println("Waiting for clients...");
                    socket = serverSocket.accept();
                    new Thread(new Patient()).start();
                    printWriter = new PrintWriter(socket.getOutputStream(), true);
                    bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));


                    // Tables for state and treatment are created
                    stateManager.addState();
                    treatmentManager.addTreatment();

                    int option;
                    try {
                        control = true;
                        while (control) {
                            System.out.println("\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                            System.out.println("@@                                                                  @@");
                            System.out.println("@@                 Welcome.                                         @@");
                            System.out.println("@@                 1. Register                                      @@");
                            System.out.println("@@                 2. Login                                         @@");
                            System.out.println("@@                 0. Exit                                          @@");
                            System.out.println("@@                                                                  @@");
                            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                            System.out.print("\nSelect an option: ");

                            try {
                                option = sc.nextInt();
                            } catch (InputMismatchException e) {
                                System.out.println("Invalid input. Please enter a number.");
                                sc.next(); // Clear the invalid input
                                continue; // Restart the loop
                            }
                            switch (option) {
                                case 1:
                                    register();
                                    break;
                                case 2:
                                    login();
                                    break;
                                case 0:
                                    conexion = false;
                                    control = false;
                                    break;
                                default:
                                    System.out.println("  NOT AN OPTION \n");
                                    break;
                            }
                        }


                    } catch (NumberFormatException e) {
                        System.out.println("  NOT A NUMBER. Closing application... \n");
                        sc.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }


                }
            } catch (IOException ex) {
                System.out.println("Error with the connection");
                ex.printStackTrace();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            releaseResourcesServer(serverSocket);
            sc.close();
        }
    }

    private static void releaseResourcesServer(ServerSocket serverSocket) {
        try {
            serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public static void register() throws SQLException {
        Scanner sc = new Scanner(System.in);
        try {
            User u = new User();

            System.out.println("Let's proceed with the registration:");

            String username, password;

            System.out.print("Username:");
            username = sc.nextLine();
            u.setUsername(username);

            System.out.print("Password:");
            password = sc.nextLine();
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] hash = md.digest();
            u.setPassword(hash);

            userManager.addUser(u); //the user is added
            login();

        } catch (NoSuchAlgorithmException ex) {
            System.out.println("Error");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void login() throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Username:");
        String username = sc.nextLine();
        System.out.print("Password:");
        String password = sc.nextLine();
        if (userManager.verifyUsername(username) && userManager.verifyPassword(username, password)) {
            User u = userManager.getUser(userManager.getId(username));
            menuUser(u);
        }
        //TODO inicilizar doctor
        //doctor = ...
    }

    public static void menuUser(User u) throws IOException {
        int userId = u.getId();
        int option;
        MedicalRecord mr = null;

        while(true) {
            printMenuDoctor();
            try {
                option = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                sc.next(); // Clear the invalid input
                continue; // Restart the loop
            }

            switch (option){
                case 1: {
                    mr = doctor.receiveMedicalRecord(socket, bufferedReader);
                }
                case 2: {
                    if (mr != null){
                        doctor.showInfoMedicalRecord(mr);
                        //TODO option to create doctor note
                        DoctorsNote dn = chooseToDoDoctorNotes(mr);
                        mr.getDoctorsNotes().add(dn);
                        chooseToSendDoctorNotes(dn);
                    } else {
                        System.out.println("No medical record detected, please select option one");
                        break;
                    }
                }
            }
        }

    }

    public static void  printMenuDoctor() {
        System.out.println("\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("@@                                                                  @@");
        System.out.println("@@                 Welcome.                                         @@");
        System.out.println("@@                 1. Receive medical record                        @@");
        System.out.println("@@                 2. Show medical record                           @@");
        System.out.println("@@                 0. Exit                                          @@");
        System.out.println("@@                                                                  @@");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.print("\nSelect an option: ");
    }

    public static DoctorsNote chooseToDoDoctorNotes(MedicalRecord mr){
        System.out.println("\nDo you want to create a doctors note? (y/n)");
        String option = sc.nextLine();
        DoctorsNote dn = null;
        if (option.equalsIgnoreCase("y")){
            dn = doctor.createDoctorsNote(mr);
        }
        else if (!option.equalsIgnoreCase("y") || option.equalsIgnoreCase("n")){
            System.out.println("Not a valid option, try again...");
            chooseToDoDoctorNotes(mr);
        }
        return dn;
    }

    public static void chooseToSendDoctorNotes(DoctorsNote dn) throws IOException {
        System.out.println("\nDo you want to send a doctors note? (y/n)");
        String option = sc.nextLine();
        if (option.equalsIgnoreCase("y")){
            doctor.sendDoctorsNote(dn, socket, printWriter);
        }
        else if (!option.equalsIgnoreCase("y") || option.equalsIgnoreCase("n")){
            System.out.println("Not a valid option, try again...");
            chooseToSendDoctorNotes(dn);
        }
    }

    public static void menuOne (){

    }
}