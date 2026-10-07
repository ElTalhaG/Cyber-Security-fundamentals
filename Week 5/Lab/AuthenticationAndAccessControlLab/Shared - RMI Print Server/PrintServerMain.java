package lab.auth;

import java.nio.file.Path;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.rmi.ssl.SslRMIServerSocketFactory;

public final class PrintServerMain {
    private PrintServerMain() { }

    public static void main(String[] args) throws Exception {
        if (args.length > 6) {
            System.err.println("Usage: PrintServerMain [users-file] [acl|rbac] [policy-file] [registry-port] [service-port] [advertised-host]");
            System.exit(2);
        }
        Path usersFile = Path.of(args.length > 0 ? args[0] : "data/users.txt");
        String mode = args.length > 1 ? args[1] : "acl";
        String defaultPolicy = "rbac".equalsIgnoreCase(mode)
                ? "Exercise 3 - Role-Based Access Control/rbac-initial.properties" : "Exercise 2 - Access Control List/acl-initial.properties";
        Path policyFile = Path.of(args.length > 2 ? args[2] : defaultPolicy);
        int registryPort = args.length > 3 ? Integer.parseInt(args[3]) : 1099;
        int servicePort = args.length > 4 ? Integer.parseInt(args[4]) : 1100;
        String advertisedHost = args.length > 5 ? args[5] : "localhost";

        String keystore = System.getenv("PRINTSERVER_KEYSTORE");
        String keystorePassword = System.getenv("PRINTSERVER_KEYSTORE_PASSWORD");
        if (keystore == null || keystorePassword == null) {
            throw new IllegalStateException("Set PRINTSERVER_KEYSTORE and PRINTSERVER_KEYSTORE_PASSWORD for TLS.");
        }
        TlsSettings.configureServerFromEnvironment();
        System.setProperty("java.rmi.server.hostname", advertisedHost);

        CredentialStore credentials = new CredentialStore(usersFile);
        AccessPolicy policy = AccessPolicy.load(mode, policyFile);
        TlsRmiClientSocketFactory clientSockets = new TlsRmiClientSocketFactory();
        SslRMIServerSocketFactory serverSockets = new SslRMIServerSocketFactory();
        Registry registry = LocateRegistry.createRegistry(registryPort, clientSockets, serverSockets);
        PrintServer service = new PrintServerImpl(credentials, policy, servicePort);
        registry.rebind("PrintServer", service);
        System.out.println("TLS print service ready at rmi://" + advertisedHost + ":" + registryPort
                + "/PrintServer (" + policy.description() + " policy " + policyFile
                + "; registry TLS enabled; service port " + servicePort + ").");
    }
}
