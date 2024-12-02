package security;

import java.security.PublicKey;
import javax.crypto.Cipher;
import java.util.Base64;

public class Encryptor {
    /**
     * Encrypts the given plain text data using an RSA public key and returns the encrypted data as a Base64-encoded string.
     *
     * @param data the plain text data to be encrypted
     * @param publicKey the RSA public key to be used for encryption
     * @return the encrypted data as a Base64-encoded {@link String}
     * @throws Exception if an error occurs during the encryption process, such as invalid key or cipher configuration
     */
    public static String encryptData(String data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }
}
