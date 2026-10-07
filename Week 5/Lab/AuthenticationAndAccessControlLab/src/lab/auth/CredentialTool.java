package lab.auth;

import java.io.Console;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.GeneralSecurityException;
import java.util.Arrays;

/** Offline demo account enrollment; run locally as the print-server administrator. */
public final class CredentialTool {
    private CredentialTool() { }

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        if (args.length != 3) {
            System.err.println("Usage: CredentialTool <users-file> <username> <USER|ADMIN>");
            System.exit(2);
        }
        Path path = Path.of(args[0]);
        String username = args[1];
        String role = args[2].toUpperCase(java.util.Locale.ROOT);
        if (!CredentialStore.validUsername(username)) {
            throw new IllegalArgumentException("Username must use 1-64 letters, digits, dots, underscores, or hyphens.");
        }
        if (!(role.equals("USER") || role.equals("ADMIN"))) {
            throw new IllegalArgumentException("Role must be USER or ADMIN.");
        }
        Console console = System.console();
        if (console == null) {
            throw new IllegalStateException("Run this tool in a terminal so passwords can be entered without echo.");
        }
        Files.createDirectories(path.toAbsolutePath().getParent());
        if (Files.exists(path)) {
            CredentialStore.requirePrivateFile(path);
            for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
                if (!line.isBlank() && !line.startsWith("#") && line.startsWith(username + ":")) {
                    throw new IllegalArgumentException("That username already exists.");
                }
            }
        } else {
            Files.createFile(path);
            CredentialStore.setOwnerOnly(path);
        }

        char[] password = console.readPassword("New password: ");
        char[] confirmation = console.readPassword("Confirm password: ");
        try {
            if (password == null || confirmation == null || password.length < 12
                    || !Arrays.equals(password, confirmation)) {
                throw new IllegalArgumentException("Passwords must match and contain at least 12 characters.");
            }
            CredentialStore.Credential credential = CredentialStore.createCredential(role, password);
            Files.writeString(path, CredentialStore.toRecord(username, credential) + System.lineSeparator(),
                    StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            CredentialStore.setOwnerOnly(path);
            System.out.println("Added " + role + " account '" + username + "'. The server must be restarted to load it.");
        } finally {
            Arrays.fill(password, '\0');
            Arrays.fill(confirmation, '\0');
        }
    }
}
