package monero.ecwid.model;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;

public abstract class EcwidPaymentDataDecoder {
    private static final Logger logger = LoggerFactory.getLogger(EcwidPaymentDataDecoder.class);

    // Mappa degli algoritmi supportati
    public static class Ciphers {
        public static final String AES_128 = "AES";
        public static final String AES_128_CBC = "AES/CBC/PKCS5Padding";
        public static final String AES_192 = "AES";
        public static final String AES_256 = "AES";
    }

    /**
     * Metodo per decifrare un testo crittografato.
     *
     * @param algorithm  Algoritmo di crittografia (es. "AES/CBC/PKCS5Padding").
     * @param key        Chiave di crittografia.
     * @param encryptedText Testo crittografato in Base64.
     * @return Il testo decifrato in formato JSON.
     * @throws Exception Se qualcosa va storto durante la decifratura.
     */
    private static String decryptText(String algorithm, String key, String encryptedText) throws Exception {
        // Decodifica il testo Base64
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);

        // Estrai IV (primi 16 byte) e payload
        byte[] iv = new byte[16];
        byte[] payload = new byte[encryptedBytes.length - 16];
        System.arraycopy(encryptedBytes, 0, iv, 0, 16);
        System.arraycopy(encryptedBytes, 16, payload, 0, payload.length);

        // Configura il cipher per la decifratura
        Cipher cipher = Cipher.getInstance(algorithm);
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

        // Decifra il payload
        byte[] decryptedBytes = cipher.doFinal(payload);
        return new String(decryptedBytes);
    }

    public static EcwidPaymentData decode(String data, String clientSecret) throws Exception {
        logger.info("decode(): clientSecret: " + clientSecret + ", data: " + data);
        // Crea una chiave di crittografia usando i primi 16 caratteri del client secret
        String encryptionKey = clientSecret.substring(0, 16);

        // Sostituzioni per il formato Base64 compatibile
        String base64Payload = data.replace("-", "+").replace("_", "/");

        // Algoritmo da utilizzare (AES-128-CBC)
        String algorithm = Ciphers.AES_128_CBC;

        // Decifra il payload
        String decryptedJson = decryptText(algorithm, encryptionKey, base64Payload);

        logger.info("decode(): decryptedJson: " + decryptedJson);

        return EcwidPaymentData.parse(decryptedJson);
    }
}
