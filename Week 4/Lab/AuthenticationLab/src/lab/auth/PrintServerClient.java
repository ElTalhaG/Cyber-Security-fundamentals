package lab.auth;

import java.io.Console;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public final class PrintServerClient {
    private final PrintServer server;
    private final Console console;
    private String sessionToken;

    private PrintServerClient(PrintServer server, Console console) {
        this.server = server;
        this.console = console;
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 2) {
            System.err.println("Usage: PrintServerClient [host] [registry-port]");
            System.exit(2);
        }
        Console console = System.console();
        if (console == null) {
            throw new IllegalStateException("Run the client in a terminal so the password is not echoed.");
        }
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 1099;
        TlsSettings.configureClientFromEnvironment();
        Registry registry = LocateRegistry.getRegistry(host, port, new TlsRmiClientSocketFactory());
        PrintServer server = (PrintServer) registry.lookup("PrintServer");
        PrintServerClient client = new PrintServerClient(server, console);
        client.login();
        client.run();
    }

    private void login() throws Exception {
        String username = console.readLine("Username: ");
        char[] password = console.readPassword("Password: ");
        try {
            sessionToken = server.login(username, password);
            System.out.println("Signed in. Session expires after 15 minutes of inactivity or 8 hours total.");
        } finally {
            if (password != null) {
                java.util.Arrays.fill(password, '\0');
            }
        }
    }

    private void run() {
        printHelp();
        while (true) {
            String line = console.readLine("print-server> ");
            if (line == null) {
                logoutQuietly();
                return;
            }
            String[] parts = line.trim().split("\\s+", 3);
            String command = parts.length == 0 ? "" : parts[0].toLowerCase(java.util.Locale.ROOT);
            if (command.isEmpty()) {
                continue;
            }
            try {
                if (command.equals("quit")) {
                    logoutQuietly();
                    return;
                } else if (command.equals("help")) {
                    printHelp();
                } else if (command.equals("login")) {
                    logoutQuietly();
                    login();
                } else if (command.equals("logout")) {
                    logoutQuietly();
                } else {
                    runOperation(command, parts);
                }
            } catch (Exception e) {
                System.out.println("Request failed: " + e.getMessage());
                if (e instanceof AuthException) {
                    System.out.println("Use 'login' to authenticate again.");
                }
            }
        }
    }

    private void runOperation(String command, String[] parts) throws Exception {
        switch (command) {
            case "print": {
                String[] args = requireTwo(parts, "print <filename> <printer>");
                int job = server.print(requireSession(), args[0], args[1]);
                System.out.println("Queued as job " + job + ".");
                break;
            }
            case "queue": {
                String printer = requireOne(parts, "queue <printer>");
                String[] jobs = server.queue(requireSession(), printer);
                if (jobs.length == 0) System.out.println("Queue is empty.");
                for (String job : jobs) System.out.println(job);
                break;
            }
            case "top": {
                String[] args = requireTwo(parts, "top <printer> <job-number>");
                boolean moved = server.topQueue(requireSession(), args[0], Integer.parseInt(args[1]));
                System.out.println(moved ? "Job moved to the front." : "No such job in that queue.");
                break;
            }
            case "start":
                server.start(requireSession()); System.out.println("Print service started."); break;
            case "stop":
                server.stop(requireSession()); System.out.println("Print service stopped."); break;
            case "restart":
                server.restart(requireSession()); System.out.println("Print service restarted; queued jobs were cleared."); break;
            case "status": {
                String printer = requireOne(parts, "status <printer>");
                System.out.println(server.status(requireSession(), printer));
                break;
            }
            case "readconfig": {
                String parameter = requireOne(parts, "readconfig <parameter>");
                System.out.println(parameter + " = " + server.readConfig(requireSession(), parameter));
                break;
            }
            case "setconfig": {
                String[] args = requireTwo(parts, "setconfig <parameter> <value>");
                server.setConfig(requireSession(), args[0], args[1]);
                System.out.println("Configuration updated.");
                break;
            }
            default:
                System.out.println("Unknown command. Type 'help'.");
        }
    }

    private String requireSession() throws AuthException {
        if (sessionToken == null) {
            throw new AuthException("Not signed in. Use 'login'.");
        }
        return sessionToken;
    }

    private static String requireOne(String[] parts, String usage) {
        if (parts.length < 2) throw new IllegalArgumentException("Usage: " + usage);
        return parts[1];
    }

    private static String[] requireTwo(String[] parts, String usage) {
        if (parts.length < 3) throw new IllegalArgumentException("Usage: " + usage);
        return new String[] { parts[1], parts[2] };
    }

    private void logoutQuietly() {
        if (sessionToken != null) {
            try {
                server.logout(sessionToken);
            } catch (Exception ignored) {
                // The server may already have expired or removed this session.
            }
            sessionToken = null;
            System.out.println("Signed out.");
        }
    }

    private static void printHelp() {
        System.out.println("Commands (file names and printer names cannot contain spaces):");
        System.out.println("  print <filename> <printer>   queue <printer>   top <printer> <job>");
        System.out.println("  start | stop | restart       status <printer>");
        System.out.println("  readconfig <parameter>       setconfig <parameter> <value>");
        System.out.println("  login | logout | help | quit");
        System.out.println("start, stop, restart, and setconfig require the ADMIN role.");
    }
}
