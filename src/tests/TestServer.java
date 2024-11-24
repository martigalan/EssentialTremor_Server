package tests;
import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TestServer {

    private static ServerSocket serverSocket;
    private static AtomicInteger activeConnections = new AtomicInteger(0); //created to count how many clients are connected
    private static boolean connection = true;

    public static void startServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Test server listening on port " + port);

        while (connection) {
            try {
                Socket clientSocket = serverSocket.accept();
                activeConnections.incrementAndGet(); //increase the number os connections until each one has connected
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(), true);

                String role = bufferedReader.readLine(); // Expect either "Patient" or "Doctor"

                if ("Patient".equals(role)) {
                    printWriter.println("Patient connected");
                    System.out.println("Patient connected: " + clientSocket.getInetAddress());
                    // Simulate patient handling logic
                } else if ("Doctor".equals(role)) {
                    printWriter.println("Doctor connected");
                    System.out.println("Doctor connected: " + clientSocket.getInetAddress());
                    // Simulate doctor handling logic
                }

                clientSocket.close();
                activeConnections.decrementAndGet(); //when a client disconnect, we decrease the number of clients connect
                checkAndShutdown();

            } catch (IOException e) {
                if (!connection) {
                    System.out.println("The test server has been closed.");
                } else {
                    System.err.println("Error when accepting connection: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Checks if there are any active connections to the server and shuts it down if none are found.
     * This method is called after a client disconnects. If the count of active connections is zero, it prints a message indicating it, stops main server and closes server socket.
     *
     * Throws @IOException while closing the server socket, it logs the error to the console.
     */
    private static void checkAndShutdown() {
        if (activeConnections.get() == 0) {
            System.out.println("No active connections. Shutting down server...");
            connection = false; // Stop the main loop
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("Error when closing server: " + e.getMessage());
            }
        }
    }

    public static void stopServer() {
        connection = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            startServer(9003);  // Start the server on port 9003
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


