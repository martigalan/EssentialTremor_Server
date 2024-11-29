package tests;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DoctorTest {

    @Test
    public void testDoctorConnection() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1); //importante porque trabajamos con hilos. Wait for thread to finish

        Thread doctorThread = new Thread(() -> {
            try (Socket socket = new Socket("localhost", 9003)) {
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Doctor message
                writer.println("Doctor");

                // Response
                String response = reader.readLine();
                System.out.println(response);

                assertEquals("Doctor connected", response);

                writer.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });

        doctorThread.start();
        latch.await();
    }
}
