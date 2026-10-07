package lab.auth;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

final class AclPolicy implements AccessPolicy {
    private static final Set<String> ALL_OPERATIONS = Set.of(
            "print", "queue", "topQueue", "start", "stop", "restart",
            "status", "readConfig", "setConfig");

    private final Map<String, Set<String>> permissions;

    private AclPolicy(Map<String, Set<String>> permissions) {
        this.permissions = Map.copyOf(permissions);
    }

    static AclPolicy load(Path path) throws IOException {
        Properties properties = readProperties(path);
        Map<String, Set<String>> permissions = new HashMap<>();
        for (String username : properties.stringPropertyNames()) {
            if (!CredentialStore.validUsername(username)) {
                throw new IOException("Invalid ACL username: " + username);
            }
            Set<String> operations = parseList(properties.getProperty(username));
            if (operations.contains("*")) {
                if (operations.size() != 1) {
                    throw new IOException("The wildcard must be the only ACL entry for " + username + ".");
                }
                operations = ALL_OPERATIONS;
            } else {
                requireKnownOperations(operations, "ACL for " + username);
            }
            permissions.put(username, Set.copyOf(operations));
        }
        return new AclPolicy(permissions);
    }

    @Override
    public boolean allows(String username, String operation) {
        return permissions.getOrDefault(username, Set.of()).contains(operation);
    }

    @Override
    public String description() {
        return "ACL";
    }

    static Set<String> parseList(String value) throws IOException {
        Set<String> result = new LinkedHashSet<>();
        if (value == null || value.isBlank()) return result;
        for (String item : value.split(",")) {
            String trimmed = item.trim();
            if (trimmed.isEmpty()) throw new IOException("Empty item in policy list.");
            result.add(trimmed);
        }
        return result;
    }

    static Properties readProperties(Path path) throws IOException {
        if (!Files.isRegularFile(path)) {
            throw new IOException("Policy file does not exist: " + path);
        }
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            properties.load(reader);
        }
        return properties;
    }

    static void requireKnownOperations(Set<String> operations, String context) throws IOException {
        for (String operation : operations) {
            if (!ALL_OPERATIONS.contains(operation)) {
                throw new IOException("Unknown operation '" + operation + "' in " + context + ".");
            }
        }
    }
}
