package lab.auth;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.rmi.ssl.SslRMIServerSocketFactory;

/** Mock print service. Every operation is mediated by session and role checks. */
final class PrintServerImpl extends UnicastRemoteObject implements PrintServer {
    private static final long serialVersionUID = 1L;
    private static final long IDLE_TTL_NANOS = 15L * 60L * 1_000_000_000L;
    private static final long ABSOLUTE_TTL_NANOS = 8L * 60L * 60L * 1_000_000_000L;
    private static final SecureRandom RANDOM = new SecureRandom();

    private static final class Session {
        final String username;
        final long createdAtNanos;
        long lastActivityNanos;

        Session(String username, long now) {
            this.username = username;
            this.createdAtNanos = now;
            this.lastActivityNanos = now;
        }
    }

    private static final class PrintJob {
        final int id;
        final String filename;
        PrintJob(int id, String filename) {
            this.id = id;
            this.filename = filename;
        }
        @Override public String toString() {
            return "!" + id + ". !" + filename;
        }
    }

    private final CredentialStore credentials;
    private final AccessPolicy policy;
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private final Map<String, Deque<PrintJob>> queues = new ConcurrentHashMap<>();
    private final Map<String, String> configuration = new ConcurrentHashMap<>();
    private final AtomicInteger nextJobId = new AtomicInteger(1);
    private volatile boolean running = true;

    PrintServerImpl(CredentialStore credentials, AccessPolicy policy, int servicePort) throws RemoteException {
        super(servicePort, new TlsRmiClientSocketFactory(), new SslRMIServerSocketFactory());
        this.credentials = credentials;
        this.policy = policy;
        configuration.put("defaultPrinter", "Laser-1");
        configuration.put("copies", "1");
    }

    @Override
    public String login(String username, char[] password) throws RemoteException, AuthException {
        char[] supplied = password == null ? new char[0] : password;
        try {
            credentials.verify(username, supplied);
            long now = System.nanoTime();
            byte[] tokenBytes = new byte[32];
            String token;
            do {
                RANDOM.nextBytes(tokenBytes);
                token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
            } while (sessions.putIfAbsent(token, new Session(username, now)) != null);
            audit(username, "LOGIN", "result=success");
            return token;
        } catch (AuthException e) {
            audit(username, "LOGIN", "result=failure");
            throw e;
        } catch (GeneralSecurityException e) {
            throw new RemoteException("Password verification failed because the configured crypto provider is unavailable.", e);
        } finally {
            java.util.Arrays.fill(supplied, '\0');
        }
    }

    @Override
    public void logout(String sessionToken) throws RemoteException, AuthException {
        Session session = requireAuthorized(sessionToken, "print");
        sessions.remove(sessionToken);
        audit(session.username, "LOGOUT", "result=success");
    }

    @Override
    public synchronized int print(String sessionToken, String filename, String printer)
            throws RemoteException, AuthException, PrintServerException {
        Session session = requireAuthorized(sessionToken, "queue");
        requireRunning();
        requireText(filename, "filename");
        requireText(printer, "printer");
        int id = nextJobId.getAndIncrement();
        Deque<PrintJob> queue = queueFor(printer);
        synchronized (queue) {
            queue.addLast(new PrintJob(id, filename));
        }
        audit(session.username, "PRINT", "printer=" + safeField(printer) + " job=" + id);
        return id;
    }

    @Override
    public synchronized String[] queue(String sessionToken, String printer)
            throws RemoteException, AuthException, PrintServerException {
        Session session = requireAuthorized(sessionToken, "topQueue");
        requireRunning();
        requireText(printer, "printer");
        Deque<PrintJob> queue = queueFor(printer);
        List<String> result = new ArrayList<>();
        synchronized (queue) {
            for (PrintJob job : queue) {
                result.add(job.toString());
            }
        }
        audit(session.username, "QUEUE", "printer=" + safeField(printer));
        return result.toArray(new String[0]);
    }

    @Override
    public synchronized boolean topQueue(String sessionToken, String printer, int jobId)
            throws RemoteException, AuthException, PrintServerException {
        Session session = requireSession(sessionToken);
        requireRunning();
        requireText(printer, "printer");
        Deque<PrintJob> queue = queueFor(printer);
        synchronized (queue) {
            PrintJob selected = null;
            for (PrintJob job : queue) {
                if (job.id == jobId) {
                    selected = job;
                    break;
                }
            }
            if (selected == null) {
                audit(session.username, "TOP_QUEUE", "printer=" + safeField(printer) + " job=" + jobId + " result=not-found");
                return false;
            }
            queue.remove(selected);
            queue.addFirst(selected);
        }
        audit(session.username, "TOP_QUEUE", "printer=" + safeField(printer) + " job=" + jobId + " result=success");
        return true;
    }

    @Override
    public synchronized void start(String sessionToken) throws RemoteException, AuthException, PrintServerException {
        Session session = requireAuthorized(sessionToken, "start");
        running = true;
        audit(session.username, "START", "result=success");
    }

    @Override
    public synchronized void stop(String sessionToken) throws RemoteException, AuthException, PrintServerException {
        Session session = requireAuthorized(sessionToken, "stop");
        running = false;
        audit(session.username, "STOP", "result=success");
    }

    @Override
    public synchronized void restart(String sessionToken) throws RemoteException, AuthException, PrintServerException {
        Session session = requireAuthorized(sessionToken, "restart");
        running = false;
        for (Deque<PrintJob> queue : queues.values()) {
            synchronized (queue) {
                queue.clear();
            }
        }
        running = true;
        audit(session.username, "RESTART", "queues=cleared result=success");
    }

    @Override
    public synchronized String status(String sessionToken, String printer)
            throws RemoteException, AuthException, PrintServerException {
        Session session = requireAuthorized(sessionToken, "status");
        requireText(printer, "printer");
        String value = running ? "RUNNING" : "STOPPED";
        audit(session.username, "STATUS", "printer=" + safeField(printer) + " state=" + value);
        return "Printer " + printer + ": " + value + " (mock service; no physical printer is contacted)";
    }

    @Override
    public String readConfig(String sessionToken, String parameter)
            throws RemoteException, AuthException, PrintServerException {
        Session session = requireAuthorized(sessionToken, "readConfig");
        requireText(parameter, "parameter");
        String value = configuration.get(parameter);
        if (value == null) {
            throw new PrintServerException("Unknown configuration parameter.");
        }
        audit(session.username, "READ_CONFIG", "parameter=" + safeField(parameter));
        return value;
    }

    @Override
    public void setConfig(String sessionToken, String parameter, String value)
            throws RemoteException, AuthException, PrintServerException {
        Session session = requireAuthorized(sessionToken, "setConfig");
        requireText(parameter, "parameter");
        requireText(value, "value");
        if (!configuration.containsKey(parameter)) {
            throw new PrintServerException("Unknown configuration parameter.");
        }
        configuration.put(parameter, value);
        audit(session.username, "SET_CONFIG", "parameter=" + safeField(parameter));
    }

    private Session requireAuthorized(String token, String operation) throws AuthException {
        Session session = requireSession(token);
        if (!policy.allows(session.username, operation)) {
            audit(session.username, "AUTHORIZATION", "result=denied operation=" + operation
                    + " policy=" + policy.description());
            throw new AuthException("Access denied for operation: " + operation + ".");
        }
        return session;
    }

    private Session requireSession(String token) throws AuthException {
        if (token == null || token.isEmpty()) {
            throw new AuthException("A valid session is required. Please sign in again.");
        }
        Session session = sessions.get(token);
        if (session == null) {
            throw new AuthException("Session is invalid or expired. Please sign in again.");
        }
        long now = System.nanoTime();
        synchronized (session) {
            if (now - session.lastActivityNanos > IDLE_TTL_NANOS
                    || now - session.createdAtNanos > ABSOLUTE_TTL_NANOS) {
                sessions.remove(token, session);
                audit(session.username, "SESSION_EXPIRED", "result=expired");
                throw new AuthException("Session is invalid or expired. Please sign in again.");
            }
            session.lastActivityNanos = now;
            return session;
        }
    }

    private Deque<PrintJob> queueFor(String printer) {
        return queues.computeIfAbsent(printer, ignored -> new ArrayDeque<>());
    }

    private void requireRunning() throws PrintServerException {
        if (!running) {
            throw new PrintServerException("Print service is stopped. An administrator must start it first.");
        }
    }

    private static void requireText(String value, String field) throws PrintServerException {
        if (value == null || value.isBlank() || value.length() > 256) {
            throw new PrintServerException(field + " must contain 1-256 non-blank characters.");
        }
    }

    private static String safeField(String value) {
        return value.replaceAll("[\\p{Cntrl}]", "?").substring(0, Math.min(value.length(), 80));
    }

    private static synchronized void audit(String username, String action, String details) {
        String safeUser = username == null ? "<unknown>" : safeField(username);
        System.out.println(Instant.now() + " user=" + safeUser + " action=" + action + " " + details);
    }
}
