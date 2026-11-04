package lab.protocol.nsl;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.util.Arrays;

/** Alice's side of the Needham-Schroeder-Lowe protocol. Use one instance per run. */
public final class AliceRole {
    private final String aliceName;
    private final byte[] aliceNameBytes;
    private final KeyPair aliceKeys;
    private final String bobName;
    private final byte[] bobNameBytes;
    private final java.security.PublicKey bobPublicKey;
    private final SecureRandom random;
    private byte[] nonceA;
    private boolean finished;

    public AliceRole(String aliceName, KeyPair aliceKeys,
                     String bobName, java.security.PublicKey bobPublicKey) {
        this(aliceName, aliceKeys, bobName, bobPublicKey, new SecureRandom());
    }

    AliceRole(String aliceName, KeyPair aliceKeys, String bobName,
              java.security.PublicKey bobPublicKey, SecureRandom random) {
        this.aliceName = aliceName;
        this.aliceNameBytes = ProtocolCrypto.nameBytes(aliceName);
        this.aliceKeys = java.util.Objects.requireNonNull(aliceKeys, "aliceKeys");
        this.bobName = bobName;
        this.bobNameBytes = ProtocolCrypto.nameBytes(bobName);
        this.bobPublicKey = java.util.Objects.requireNonNull(bobPublicKey, "bobPublicKey");
        this.random = java.util.Objects.requireNonNull(random, "random");
    }

    /** Creates message 1: {NA, A} encrypted for Bob. */
    public byte[] step1() throws GeneralSecurityException {
        if (nonceA != null) {
            throw new IllegalStateException("step1 may only be called once per AliceRole");
        }
        nonceA = ProtocolCrypto.randomNonce(random);
        byte[] plaintext = ProtocolCrypto.concatenate(nonceA, aliceNameBytes);
        return ProtocolCrypto.encrypt(plaintext, bobPublicKey);
    }

    /** Checks message 2 and creates message 3: {NB} encrypted for Bob. */
    public byte[] step2_3(byte[] message2) throws GeneralSecurityException {
        if (nonceA == null) {
            throw new IllegalStateException("Call step1 before step2_3");
        }
        if (finished) {
            throw new IllegalStateException("This AliceRole has already completed its run");
        }

        byte[] plaintext = ProtocolCrypto.decrypt(message2, aliceKeys.getPrivate());
        int minimumLength = 2 * ProtocolCrypto.NONCE_BYTES + 1;
        if (plaintext.length < minimumLength) {
            throw new IllegalArgumentException("Message 2 is too short");
        }

        int nonceLength = ProtocolCrypto.NONCE_BYTES;
        byte[] receivedNonceA = ProtocolCrypto.field(plaintext, 0, nonceLength);
        byte[] nonceB = ProtocolCrypto.field(plaintext, nonceLength, nonceLength);
        byte[] receivedBobName = ProtocolCrypto.remainder(plaintext, 2 * nonceLength);

        if (!ProtocolCrypto.sameSecret(nonceA, receivedNonceA)) {
            throw new IllegalArgumentException("Message 2 contains the wrong NA");
        }
        if (!ProtocolCrypto.samePublicName(bobNameBytes, receivedBobName)) {
            throw new IllegalArgumentException("Message 2 does not name the expected Bob");
        }

        finished = true;
        return ProtocolCrypto.encrypt(nonceB, bobPublicKey);
    }

    public String getAliceName() {
        return aliceName;
    }

    public String getExpectedBobName() {
        return bobName;
    }
}
