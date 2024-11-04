import jdbc.*;
import pojos.User;

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

    public static void main(String[] args) {
        connectionManager= new ConnectionManager();
        userManager = new JDBCUserManager(connectionManager);
        doctorManager = new JDBCDoctorManager(connectionManager);
        doctorNotesManager = new JDBCDoctorNotes(connectionManager);
        medicalRecordManager = new JDBCMedicalRecordManager(connectionManager);
        patientManager = new JDBCPatientManager(connectionManager);
        stateManager = new JDBCStateManager(connectionManager);
        treatmentManager = new JDBCTreatmentManager(connectionManager);
        // Tables for state and treatment are created
        stateManager.addState();
        treatmentManager.addTreatment();

        int option;
        try {
            control = true;
            while (control)  {
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
                        control = false;
                        break;
                    default:
                        System.out.println("  NOT AN OPTION \n");
                        break;
                }
            }

        }catch (NumberFormatException e) {
            System.out.println("  NOT A NUMBER. Closing application... \n");
            sc.close();
        }catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }finally {
            sc.close();
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

        }catch (NoSuchAlgorithmException ex) {
            System.out.println("Error");
        }

    }

    public static void login() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Username:");
        String username = sc.nextLine();
        System.out.print("Password:");
        String password = sc.nextLine();
        if (userManager.verifyUsername(username) && userManager.verifyPassword(username, password)) {
            User u= userManager.getUser(userManager.getId(username));
            menuUser(u);
        }
    }

    public static void menuUser(User u){
        int userId = u.getId();




    }
}