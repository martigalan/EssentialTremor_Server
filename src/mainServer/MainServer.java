package mainServer;

import jdbc.*;
import pojos.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainServer {

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
     * Scanner for user input
     */
    private static Scanner sc = new Scanner(System.in);
    /**
     * Server socket waiting for connexions
     */
    private static ServerSocket serverSocket;
    /**
     * Client socket (either doctor or patient)
     */
    private static Socket clientSocket;
    /**
     * Output control
     */
    private static PrintWriter printWriter;
    /**
     * Input control
     */
    private static BufferedReader bufferedReader;
    /**
     * Counter for active connexions
     */
    private static AtomicInteger activeConnections = new AtomicInteger(0);
    /**
     * Control for connexions
     */
    private static boolean connection;

    /**
     * Main for the server.
     * Waits for connexion and creates threads for mutliple clients. It derives the client to their respective logics depending on their role (patient or doctor).
     *
     * @param args
     */
    public static void main(String[] args) {
        serverSocket = null;
        printWriter = null;
        bufferedReader = null;
        connection = true;
        int port = 9000;

        try {
            connectionManager = new ConnectionManager();
            userManager = new JDBCUserManager(connectionManager);
            doctorManager = new JDBCDoctorManager(connectionManager);
            doctorNotesManager = new JDBCDoctorNotesManager(connectionManager);
            medicalRecordManager = new JDBCMedicalRecordManager(connectionManager);
            patientManager = new JDBCPatientManager(connectionManager);
            stateManager = new JDBCStateManager(connectionManager);
            stateManager.addState();
            treatmentManager = new JDBCTreatmentManager(connectionManager);
            treatmentManager.addTreatment();

            //Create socket
            serverSocket = new ServerSocket(port);
            System.out.println("Server listening in port " + port);

            while (connection) {
                try {
                    clientSocket = serverSocket.accept();
                    activeConnections.incrementAndGet();
                    //System.out.println("Client connected: " + clientSocket.getInetAddress());
                    bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    printWriter = new PrintWriter(clientSocket.getOutputStream());
                    System.out.println("Active conexions: " + activeConnections.get());

                    String role = bufferedReader.readLine();
                    if (role.equals("Patient")) {
                        System.out.println("Patient connected: " + clientSocket.getInetAddress());
                        PatientHandler patientHandler = new PatientHandler(clientSocket, connectionManager);
                        //new Thread(patientHandler).start();
                        new Thread(() -> {
                            patientHandler.run();
                            activeConnections.decrementAndGet(); // Decrements when thread finishes
                            checkAndShutdown();
                        }).start();
                    } else if (role.equals("Doctor")) {
                        System.out.println("Doctor connected: " + clientSocket.getInetAddress());
                        DoctorHandler doctorHandler = new DoctorHandler(clientSocket, connectionManager);
                        //new Thread(doctorHandler).start();
                        new Thread(() -> {
                            doctorHandler.run();
                            activeConnections.decrementAndGet(); // Decrementa al finalizar el hilo
                            checkAndShutdown();
                        }).start();
                    }
                } catch (IOException e) {
                    if (!connection) {
                        System.out.println("The server has been closed.");
                    } else {
                        System.err.println("Error when accepting connexion: " + e.getMessage());
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            releaseResourcesServer(serverSocket, bufferedReader, printWriter);
            sc.close();
        }
    }

    /**
     * Checks if there's any active connexions
     */
    private static void checkAndShutdown() {
        if (activeConnections.get() == 0) {
            System.out.println("No active connexions. Closing down server...");
            connection = false; // Detiene el bucle principal
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("Error when closing server: " + e.getMessage());
            }
        }
    }

    /**
     * Closes the resources.
     *
     * @param serverSocket
     */
    private static void releaseResourcesServer(ServerSocket serverSocket, BufferedReader bufferedReader, PrintWriter printWriter) {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            bufferedReader.close();
            printWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
