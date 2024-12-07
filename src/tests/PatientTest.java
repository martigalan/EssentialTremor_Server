package tests;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PatientTest {

    @Test
    public void testPatientConnection() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);  // Wait for thread to finish

        Thread patientThread = new Thread(() -> {
            try (Socket socket = new Socket("localhost", 9003)) {
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Patient message
                writer.println("Patient");

                // Response
                String response = reader.readLine();
                System.out.println(response);

                assertEquals("Patient connected", response);

                writer.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });

        patientThread.start();
        latch.await();
    }
}
