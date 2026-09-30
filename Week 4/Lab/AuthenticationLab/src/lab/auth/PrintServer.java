package lab.auth;

import java.rmi.Remote;
import java.rmi.RemoteException;

/** Remote interface. Every operation after login carries an opaque session token. */
public interface PrintServer extends Remote {
    String login(String username, char[] password) throws RemoteException, AuthException;
    void logout(String sessionToken) throws RemoteException, AuthException;

    int print(String sessionToken, String filename, String printer)
            throws RemoteException, AuthException, PrintServerException;
    String[] queue(String sessionToken, String printer)
            throws RemoteException, AuthException, PrintServerException;
    boolean topQueue(String sessionToken, String printer, int job)
            throws RemoteException, AuthException, PrintServerException;
    void start(String sessionToken) throws RemoteException, AuthException, PrintServerException;
    void stop(String sessionToken) throws RemoteException, AuthException, PrintServerException;
    void restart(String sessionToken) throws RemoteException, AuthException, PrintServerException;
    String status(String sessionToken, String printer)
            throws RemoteException, AuthException, PrintServerException;
    String readConfig(String sessionToken, String parameter)
            throws RemoteException, AuthException, PrintServerException;
    void setConfig(String sessionToken, String parameter, String value)
            throws RemoteException, AuthException, PrintServerException;
}
