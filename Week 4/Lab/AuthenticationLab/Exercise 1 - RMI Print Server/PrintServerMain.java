package lab.auth;

import java.nio.file.Path;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.rmi.ssl.SslRMIServerSocketFactory;

public final class PrintServerMain {
    private PrintServerMain() { }

    public static void main(String[] args) throws Exception {
        if (args.length > 4) {
            System.err.println("Usage: PrintServerMain [users-file] [registry-port] [service-port] [advertised-host]");
            System.exit(2);
        }
        Path usersFile = Path.of(args.length > 0 ? args[0] : "data/users.txt");
        int registryPort = args.length > 1 ? Integer.parseInt(args[1]) : 1099;
        int servicePort = args.length > 2 ? Integer.parseInt(args[2]) : 1100;
        String advertisedHost = args.length > 3 ? args[3] : "localhost";

        String keystore = System.getenv("PRINTSERVER_KEYSTORE");
        String keystorePassword = System.getenv("PRINTSERVER_KEYSTORE_PASSWORD");
        if (keystore == null || keystorePassword == null) {
            throw new IllegalStateException("Set PRINTSERVER_KEYSTORE and PRINTSERVER_KEYSTORE_PASSWORD for TLS.");
        }
        TlsSettings.configureServerFromEnvironment();
        System.setProperty("java.rmi.server.hostname", advertisedHost);

        CredentialStore credentials = new CredentialStore(usersFile);
        TlsRmiClientSocketFactory clientSockets = new TlsRmiClientSocketFactory();
        SslRMIServerSocketFactory serverSockets = new SslRMIServerSocketFactory();
        Registry registry = LocateRegistry.createRegistry(registryPort, clientSockets, serverSockets);
        PrintServer service = new PrintServerImpl(credentials, servicePort);
        registry.rebind("PrintServer", service);
        System.out.println("TLS print service ready at rmi://" + advertisedHost + ":" + registryPort
                + "/PrintServer (registry TLS enabled; service port " + servicePort + ").");
    }
}
