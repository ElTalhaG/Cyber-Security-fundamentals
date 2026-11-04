package lab.protocol.nsl;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

/** Runs one local Alice-Bob exchange by passing each returned message to the other role. */
public final class NslDemo {
    private NslDemo() {}

    public static void main(String[] args) throws GeneralSecurityException {
        KeyPair aliceKeys = createRsaKeyPair();
        KeyPair bobKeys = createRsaKeyPair();

        AliceRole alice = new AliceRole("Alice", aliceKeys, "Bob", bobKeys.getPublic());
        BobRole bob = new BobRole("Bob", bobKeys, "Alice", aliceKeys.getPublic());

        byte[] message1 = alice.step1();
        byte[] message2 = bob.step1(message1);
        byte[] message3 = alice.step2_3(message2);
        bob.step3(message3);

        System.out.println("NSL exchange completed: Alice and Bob accepted the expected nonces and identities.");
        System.out.println("No network was used; each returned byte array was passed directly to the next role.");
    }

    private static KeyPair createRsaKeyPair() throws GeneralSecurityException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }
}
