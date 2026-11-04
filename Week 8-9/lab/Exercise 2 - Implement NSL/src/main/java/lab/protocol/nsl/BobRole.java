package lab.protocol.nsl;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.SecureRandom;

/** Bob's side of the Needham-Schroeder-Lowe protocol. Use one instance per run. */
public final class BobRole {
    private final String bobName;
    private final byte[] bobNameBytes;
    private final KeyPair bobKeys;
    private final String aliceName;
    private final byte[] aliceNameBytes;
    private final java.security.PublicKey alicePublicKey;
    private final SecureRandom random;
    private byte[] nonceB;
    private boolean finished;

    public BobRole(String bobName, KeyPair bobKeys,
                   String aliceName, java.security.PublicKey alicePublicKey) {
        this(bobName, bobKeys, aliceName, alicePublicKey, new SecureRandom());
    }

    BobRole(String bobName, KeyPair bobKeys, String aliceName,
            java.security.PublicKey alicePublicKey, SecureRandom random) {
        this.bobName = bobName;
        this.bobNameBytes = ProtocolCrypto.nameBytes(bobName);
        this.bobKeys = java.util.Objects.requireNonNull(bobKeys, "bobKeys");
        this.aliceName = aliceName;
        this.aliceNameBytes = ProtocolCrypto.nameBytes(aliceName);
        this.alicePublicKey = java.util.Objects.requireNonNull(alicePublicKey, "alicePublicKey");
        this.random = java.util.Objects.requireNonNull(random, "random");
    }

    /** Checks message 1 and creates message 2: {NA, NB, B} encrypted for Alice. */
    public byte[] step1(byte[] message1) throws GeneralSecurityException {
        if (nonceB != null) {
            throw new IllegalStateException("Bob has already started this run");
        }

        byte[] plaintext = ProtocolCrypto.decrypt(message1, bobKeys.getPrivate());
        int nonceLength = ProtocolCrypto.NONCE_BYTES;
        if (plaintext.length <= nonceLength) {
            throw new IllegalArgumentException("Message 1 is too short");
        }
        byte[] nonceA = ProtocolCrypto.field(plaintext, 0, nonceLength);
        byte[] receivedAliceName = ProtocolCrypto.remainder(plaintext, nonceLength);
        if (!ProtocolCrypto.samePublicName(aliceNameBytes, receivedAliceName)) {
            throw new IllegalArgumentException("Message 1 does not name the expected Alice");
        }

        nonceB = ProtocolCrypto.randomNonce(random);
        byte[] response = ProtocolCrypto.concatenate(nonceA, nonceB, bobNameBytes);
        return ProtocolCrypto.encrypt(response, alicePublicKey);
    }

    /** Checks message 3 and confirms Alice returned Bob's fresh challenge NB. */
    public void step3(byte[] message3) throws GeneralSecurityException {
        if (nonceB == null) {
            throw new IllegalStateException("Call step1 before step3");
        }
        if (finished) {
            throw new IllegalStateException("This BobRole has already completed its run");
        }

        byte[] receivedNonceB = ProtocolCrypto.decrypt(message3, bobKeys.getPrivate());
        if (!ProtocolCrypto.sameSecret(nonceB, receivedNonceB)) {
            throw new IllegalArgumentException("Message 3 contains the wrong NB");
        }
        finished = true;
    }

    public String getBobName() {
        return bobName;
    }

    public String getExpectedAliceName() {
        return aliceName;
    }
}
