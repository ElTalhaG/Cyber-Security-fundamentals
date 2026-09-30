package lab.auth;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.LinkOption;
import java.nio.file.attribute.PosixFilePermission;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Read-only credential database using salted, deliberately expensive password hashes. */
final class CredentialStore {
    static final int ITERATIONS = 600_000;
    private static final int SALT_BYTES = 16;
    private static final int HASH_BYTES = 32;
    private static final SecureRandom RANDOM = new SecureRandom();

    static final class Credential {
        final String role;
        final byte[] salt;
        final byte[] hash;
        final int iterations;

        Credential(String role, byte[] salt, byte[] hash, int iterations) {
            this.role = role;
            this.salt = salt.clone();
            this.hash = hash.clone();
            this.iterations = iterations;
        }
    }

    private final Map<String, Credential> credentials;
    private final byte[] dummySalt;
    private final byte[] dummyHash;

    CredentialStore(Path path) throws IOException, GeneralSecurityException {
        requirePrivateFile(path);
        this.credentials = load(path);
        if (credentials.isEmpty()) {
            throw new IOException("The user file contains no accounts. Add an account with CredentialTool first.");
        }
        dummySalt = new byte[SALT_BYTES];
        RANDOM.nextBytes(dummySalt);
        dummyHash = derive("not-a-real-user-password".toCharArray(), dummySalt, ITERATIONS);
    }

    String verify(String username, char[] password) throws AuthException, GeneralSecurityException {
        Credential credential = credentials.get(username);
        byte[] expected = credential == null ? dummyHash : credential.hash;
        byte[] salt = credential == null ? dummySalt : credential.salt;
        int iterations = credential == null ? ITERATIONS : credential.iterations;
        byte[] actual = derive(password, salt, iterations);
        boolean matches = MessageDigest.isEqual(expected, actual);
        java.util.Arrays.fill(actual, (byte) 0);
        if (credential == null || !matches) {
            throw new AuthException("Invalid username or password.");
        }
        return credential.role;
    }

    static Credential createCredential(String role, char[] password)
            throws GeneralSecurityException {
        byte[] salt = new byte[SALT_BYTES];
        RANDOM.nextBytes(salt);
        return new Credential(role, salt, derive(password, salt, ITERATIONS), ITERATIONS);
    }

    private static byte[] derive(char[] password, byte[] salt, int iterations)
            throws GeneralSecurityException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, HASH_BYTES * 8);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
        } finally {
            spec.clearPassword();
        }
    }

    private static Map<String, Credential> load(Path path) throws IOException {
        Map<String, Credential> result = new HashMap<>();
        int lineNumber = 0;
        for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
            lineNumber++;
            if (line.isBlank() || line.startsWith("#")) {
                continue;
            }
            String[] fields = line.split(":", -1);
            if (fields.length != 5 || !validUsername(fields[0])
                    || !(fields[1].equals("USER") || fields[1].equals("ADMIN"))) {
                throw new IOException("Invalid user record on line " + lineNumber + ".");
            }
            try {
                int iterations = Integer.parseInt(fields[2]);
                byte[] salt = Base64.getDecoder().decode(fields[3]);
                byte[] hash = Base64.getDecoder().decode(fields[4]);
                if (iterations < 100_000 || iterations > 2_000_000
                        || salt.length < SALT_BYTES || hash.length != HASH_BYTES) {
                    throw new IllegalArgumentException("Invalid credential parameters");
                }
                Credential prior = result.put(fields[0], new Credential(fields[1], salt, hash, iterations));
                if (prior != null) {
                    throw new IOException("Duplicate username on line " + lineNumber + ".");
                }
            } catch (IllegalArgumentException e) {
                throw new IOException("Invalid credential data on line " + lineNumber + ".", e);
            }
        }
        return Map.copyOf(result);
    }

    static boolean validUsername(String username) {
        return username != null && username.matches("[A-Za-z0-9._-]{1,64}");
    }

    static void requirePrivateFile(Path path) throws IOException {
        if (!Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)) {
            throw new IOException("Credential file does not exist or is not a regular file: " + path);
        }
        try {
            EnumSet<PosixFilePermission> permissions = EnumSet.noneOf(PosixFilePermission.class);
            permissions.addAll(Files.getPosixFilePermissions(path));
            permissions.retainAll(EnumSet.of(PosixFilePermission.GROUP_READ,
                    PosixFilePermission.GROUP_WRITE, PosixFilePermission.GROUP_EXECUTE,
                    PosixFilePermission.OTHERS_READ, PosixFilePermission.OTHERS_WRITE,
                    PosixFilePermission.OTHERS_EXECUTE));
            if (!permissions.isEmpty()) {
                throw new IOException("Credential file must be owner-only (chmod 600): " + path);
            }
        } catch (UnsupportedOperationException e) {
            System.err.println("Warning: POSIX file permissions could not be checked; protect the credential file with OS ACLs.");
        }
    }

    static void setOwnerOnly(Path path) throws IOException {
        try {
            Files.setPosixFilePermissions(path, EnumSet.of(
                    PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE));
        } catch (UnsupportedOperationException e) {
            System.err.println("Warning: set owner-only file permissions with the operating system ACL tools.");
        }
    }

    static String toRecord(String username, Credential credential) {
        Base64.Encoder encoder = Base64.getEncoder();
        return username + ":" + credential.role + ":" + credential.iterations + ":"
                + encoder.encodeToString(credential.salt) + ":"
                + encoder.encodeToString(credential.hash);
    }
}
