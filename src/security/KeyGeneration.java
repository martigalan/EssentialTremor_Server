package security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class KeyGeneration {
    /**
     * Generates an RSA key pair, saves the public and private keys to files, and returns the generated key pair.
     *
     * The keys are saved in the following files:
     * - Public key: "server_public_key.pem"
     * - Private key: "server_private_key.pem"
     *
     * @return a {@link KeyPair} containing the generated RSA public and private keys
     * @throws Exception if an error occurs during key generation or file operations
     */
    public static KeyPair generateKeys() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        // Guardar la clave pública
        Files.write(Paths.get("server_public_key.pem"), keyPair.getPublic().getEncoded());
        // Guardar la clave privada
        Files.write(Paths.get("server_private_key.pem"), keyPair.getPrivate().getEncoded());

        return keyPair;
    }

    /**
     * Converts the public key of a given RSA key pair into a Base64-encoded string.
     *
     * @param keyPair the {@link KeyPair} containing the RSA public key
     * @return a Base64-encoded string representation of the public key
     */
    public static String getPublicKeyAsString(KeyPair keyPair) {
        return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }
}
