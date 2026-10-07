package lab.auth;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

final class RbacPolicy implements AccessPolicy {
    private final Map<String, Set<String>> userRoles;
    private final Map<String, Set<String>> rolePermissions;
    private final Map<String, Set<String>> inheritance;

    private RbacPolicy(Map<String, Set<String>> userRoles,
                       Map<String, Set<String>> rolePermissions,
                       Map<String, Set<String>> inheritance) {
        this.userRoles = Map.copyOf(userRoles);
        this.rolePermissions = Map.copyOf(rolePermissions);
        this.inheritance = Map.copyOf(inheritance);
    }

    static RbacPolicy load(Path path) throws IOException {
        Properties properties = AclPolicy.readProperties(path);
        Map<String, Set<String>> users = new HashMap<>();
        Map<String, Set<String>> roles = new HashMap<>();
        Map<String, Set<String>> parents = new HashMap<>();

        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            if (key.startsWith("user.")) {
                String username = key.substring("user.".length());
                if (!CredentialStore.validUsername(username)) {
                    throw new IOException("Invalid RBAC username: " + username);
                }
                users.put(username, AclPolicy.parseList(value));
            } else if (key.startsWith("role.")) {
                String role = key.substring("role.".length());
                if (role.isBlank()) throw new IOException("RBAC role name cannot be empty.");
                Set<String> operations = AclPolicy.parseList(value);
                if (operations.contains("*")) {
                    if (operations.size() != 1) {
                        throw new IOException("The wildcard must be the only permission for role " + role + ".");
                    }
                    operations = Set.of("*");
                } else {
                    AclPolicy.requireKnownOperations(operations, "role " + role);
                }
                roles.put(role, Set.copyOf(operations));
            } else if (key.startsWith("inherits.")) {
                String role = key.substring("inherits.".length());
                if (role.isBlank()) throw new IOException("RBAC inheritance role name cannot be empty.");
                parents.put(role, AclPolicy.parseList(value));
            } else {
                throw new IOException("Unknown property in RBAC policy: " + key);
            }
        }

        if (roles.isEmpty()) throw new IOException("RBAC policy defines no roles.");
        for (Map.Entry<String, Set<String>> entry : users.entrySet()) {
            requireKnownRoles(entry.getValue(), roles, "user " + entry.getKey());
        }
        for (Map.Entry<String, Set<String>> entry : parents.entrySet()) {
            if (!roles.containsKey(entry.getKey())) {
                throw new IOException("Inheritance is defined for unknown role: " + entry.getKey());
            }
            requireKnownRoles(entry.getValue(), roles, "inheritance of " + entry.getKey());
        }
        rejectCycles(roles.keySet(), parents);
        return new RbacPolicy(users, roles, parents);
    }

    @Override
    public boolean allows(String username, String operation) {
        Set<String> assignedRoles = userRoles.get(username);
        if (assignedRoles == null) return false;
        for (String role : assignedRoles) {
            if (allowsThroughRole(role, operation, new HashSet<>())) return true;
        }
        return false;
    }

    private boolean allowsThroughRole(String role, String operation, Set<String> visited) {
        if (!visited.add(role)) return false;
        Set<String> operations = rolePermissions.getOrDefault(role, Set.of());
        if (operations.contains("*") || operations.contains(operation)) return true;
        for (String parent : inheritance.getOrDefault(role, Set.of())) {
            if (allowsThroughRole(parent, operation, visited)) return true;
        }
        return false;
    }

    @Override
    public String description() {
        return "RBAC";
    }

    private static void requireKnownRoles(Set<String> names, Map<String, Set<String>> roles,
                                         String context) throws IOException {
        for (String role : names) {
            if (!roles.containsKey(role)) {
                throw new IOException("Unknown role '" + role + "' in " + context + ".");
            }
        }
    }

    private static void rejectCycles(Set<String> roles, Map<String, Set<String>> parents)
            throws IOException {
        Set<String> complete = new HashSet<>();
        Deque<String> active = new ArrayDeque<>();
        for (String role : roles) visit(role, parents, complete, active);
    }

    private static void visit(String role, Map<String, Set<String>> parents,
                              Set<String> complete, Deque<String> active) throws IOException {
        if (complete.contains(role)) return;
        if (active.contains(role)) throw new IOException("Cycle in role hierarchy involving " + role + ".");
        active.push(role);
        for (String parent : parents.getOrDefault(role, Set.of())) {
            visit(parent, parents, complete, active);
        }
        active.pop();
        complete.add(role);
    }
}
