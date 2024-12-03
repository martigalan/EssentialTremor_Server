package security;

import java.security.PrivateKey;
import javax.crypto.Cipher;
import java.util.Base64;

public class Decryptor {
    /**
     * Decrypts the given Base64-encoded, RSA-encrypted data using the specified private key.
     *
     * @param encryptedData the encrypted data as a Base64-encoded string
     * @param privateKey the RSA private key to be used for decryption
     * @return the decrypted plain text as a {@link String}
     * @throws Exception if an error occurs during decryption, such as invalid key or cipher configuration
     */
    public static String decryptData(String encryptedData, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedData);
    }
}

