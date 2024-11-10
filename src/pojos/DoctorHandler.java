package pojos;

import data.ACC;
import data.EMG;
import jdbc.ConnectionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

public class DoctorHandler implements Runnable{
    private static Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public static ConnectionManager connectionManager;

    public DoctorHandler(Socket clientSocket, ConnectionManager dbConnection) {
        this.socket = clientSocket;
        this.connectionManager = dbConnection;
    }
    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

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
        }
    }
}
