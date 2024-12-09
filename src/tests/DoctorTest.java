package tests;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Contiene prueba automatizada para verificar si el servidor maneja correctamente las conexiones de clientes que se identifican como "Doctor"
 */
public class DoctorTest {

    @Test
    public void testDoctorConnection() throws InterruptedException {
        /**
         * instancia de CountDownLatch para sincronizar hilo principal con el hilo que ejecuta la prueba
         */
        CountDownLatch latch = new CountDownLatch(1); //importante porque trabajamos con hilos. Wait for thread to finish

        Thread doctorThread = new Thread(() -> {
            try (Socket socket = new Socket("localhost", 9003)) {
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Doctor message -> aquí le decimos al server quien soy
                writer.println("Doctor");

                // Response
                String response = reader.readLine();
                System.out.println(response);  //Doctor connected (if all is right)

                //verifies if "Doctor connected" using this method (assertEquals is used to probe threads)
                assertEquals("Doctor connected", response);

                /*
                String response = connectAndSendRole("Doctor");
                System.out.println(response);
                assertEquals("Doctor connected", response);
                 */

                writer.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                latch.countDown(); //cierro hilo si hubiera algún problema
            }
        });

        doctorThread.start();
        latch.await();//para esperar a que el hilo termine antes de concluir la prueba
    }
}
