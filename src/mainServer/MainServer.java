package mainServer;

import jdbc.*;
import pojos.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainServer {

    public static ConnectionManager connectionManager;
    public static JDBCUserManager userManager;
    public static JDBCDoctorManager doctorManager;
    public static JDBCDoctorNotesManager doctorNotesManager;
    public static JDBCMedicalRecordManager medicalRecordManager;
    public static JDBCPatientManager patientManager;
    public static JDBCStateManager stateManager;
    public static JDBCTreatmentManager treatmentManager;
    private static boolean control;
    private static Scanner sc = new Scanner(System.in);
    private static Doctor doctor;
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static PrintWriter printWriter;
    private static BufferedReader bufferedReader;

    public static void main(String[] args) {
        serverSocket = null;
        printWriter = null;
        bufferedReader = null;
        boolean connection = true;
        int port = 9000;

        try {
            connectionManager = new ConnectionManager();
            userManager = new JDBCUserManager(connectionManager);
            doctorManager = new JDBCDoctorManager(connectionManager);
            doctorNotesManager = new JDBCDoctorNotesManager(connectionManager);
            medicalRecordManager = new JDBCMedicalRecordManager(connectionManager);
            patientManager = new JDBCPatientManager(connectionManager);
            stateManager = new JDBCStateManager(connectionManager);
            treatmentManager = new JDBCTreatmentManager(connectionManager);

            //Create socket
            serverSocket = new ServerSocket(port);
            System.out.println("Server listening in port " + port);

            //TODO preguntar si, para crear los hilos por separado debemos hacernos un ClientHandler, que haga registro y login
            //y según el rol, me cree un paciente y un doctor
            /*
            while (connection) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                //Create thread to manage connection
                new Thread(new ClientHandler(clientSocket)).start();
            }*/
            //TODO otra opción podría ser esta?
            /*
                while (connection) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                //Create thread to manage connection
                PatientHandler patientHandler = new PatientHandler(clientSocket);
                new Thread(patientHandler).start();
                DoctorHandler doctorHandler = new DoctorHandler(clientSocket);
                new Thread(doctorHandler).start();
            }
             */
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
            Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


}
