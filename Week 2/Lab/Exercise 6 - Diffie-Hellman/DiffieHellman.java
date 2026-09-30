import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import javax.crypto.KeyAgreement;

/** Basic finite-field Diffie-Hellman exchange for Week 2, Exercise 6. */
public class DiffieHellman {
    private static byte[] derive(KeyPair own, java.security.PublicKey other) throws Exception {
        KeyAgreement agreement = KeyAgreement.getInstance("DH");
        agreement.init(own.getPrivate());
        agreement.doPhase(other, true);
        return agreement.generateSecret();
    }

    private static String sha256(byte[] input) throws Exception {
        byte[] digest = MessageDigest.getInstance("SHA-256").digest(input);
        StringBuilder out = new StringBuilder();
        for (byte value : digest) out.append(String.format("%02x", value));
        return out.toString();
    }

    public static void main(String[] args) throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("DH");
        generator.initialize(2048);
        KeyPair alice = generator.generateKeyPair();
        KeyPair bob = generator.generateKeyPair();
        byte[] aliceSecret = derive(alice, bob.getPublic());
        byte[] bobSecret = derive(bob, alice.getPublic());
        boolean equal = MessageDigest.isEqual(aliceSecret, bobSecret);
        System.out.println("Shared secrets match: " + equal);
        System.out.println("Alice derived-key fingerprint (SHA-256): " + sha256(aliceSecret));
        System.out.println("Bob derived-key fingerprint (SHA-256):   " + sha256(bobSecret));
        if (!equal) throw new IllegalStateException("Diffie-Hellman secrets did not match");
    }
}
