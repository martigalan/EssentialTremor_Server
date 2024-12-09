package tests;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Contiene prueba automatizada para verificar si el servidor maneja correctamente las conexiones de clientes que se identifican como "Patient"
 */
public class PatientTest {

    @Test
    public void testPatientConnection() throws InterruptedException {
        /**
         * instancia de CountDownLatch para sincronizar hilo principal con el hilo que ejecuta la prueba
         */
        CountDownLatch latch = new CountDownLatch(1);  // Wait for thread to finish

        Thread patientThread = new Thread(() -> {
            try (Socket socket = new Socket("localhost", 9003)) {
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Patient message -> aquí le decimos al server quien soy
                writer.println("Patient");

                // Response
                String response = reader.readLine();
                System.out.println(response); //Patient connected (if all is right)

                //verifies if "Patient connected" using this method (assertEquals is used to probe threads)
                assertEquals("Patient connected", response);

                /*
                String response = connectAndSendRole("Patient");
                System.out.println(response);
                assertEquals("Patient connected", response);
                 */

                writer.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                latch.countDown(); //cierro hilo si hubiera algún problema
            }
        });

        patientThread.start();
        latch.await(); //para esperar a que el hilo termine antes de concluir la prueba
    }
}
