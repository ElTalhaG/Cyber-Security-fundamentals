package lab.protocol.nsl;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.security.spec.MGF1ParameterSpec;

/** Small encoding and RSA-OAEP helpers for the three-message NSL exercise. */
final class ProtocolCrypto {
    static final int NONCE_BYTES = 32;
    private static final int MAX_NAME_BYTES = 64;
    private static final String RSA_OAEP = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    private static final OAEPParameterSpec OAEP_SHA256 = new OAEPParameterSpec(
            "SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);

    private ProtocolCrypto() {}

    static byte[] randomNonce(SecureRandom random) {
        byte[] nonce = new byte[NONCE_BYTES];
        random.nextBytes(nonce);
        return nonce;
    }

    static byte[] nameBytes(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Agent name must not be empty");
        }
        byte[] bytes = name.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > MAX_NAME_BYTES) {
            throw new IllegalArgumentException("Agent name is too long");
        }
        return bytes;
    }

    static byte[] concatenate(byte[]... fields) {
        int length = 0;
        for (byte[] field : fields) {
            length = Math.addExact(length, field.length);
        }
        byte[] result = new byte[length];
        int offset = 0;
        for (byte[] field : fields) {
            System.arraycopy(field, 0, result, offset, field.length);
            offset += field.length;
        }
        return result;
    }

    static byte[] encrypt(byte[] message, java.security.PublicKey recipient)
            throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(RSA_OAEP);
        cipher.init(Cipher.ENCRYPT_MODE, recipient, OAEP_SHA256);
        return cipher.doFinal(message);
    }

    static byte[] decrypt(byte[] ciphertext, java.security.PrivateKey recipient)
            throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(RSA_OAEP);
        cipher.init(Cipher.DECRYPT_MODE, recipient, OAEP_SHA256);
        return cipher.doFinal(ciphertext);
    }

    static boolean sameSecret(byte[] expected, byte[] actual) {
        return MessageDigest.isEqual(expected, actual);
    }

    static boolean samePublicName(byte[] expected, byte[] actual) {
        return Arrays.equals(expected, actual);
    }

    static byte[] field(byte[] message, int offset, int length) {
        return Arrays.copyOfRange(message, offset, offset + length);
    }

    static byte[] remainder(byte[] message, int offset) {
        return Arrays.copyOfRange(message, offset, message.length);
    }
}
