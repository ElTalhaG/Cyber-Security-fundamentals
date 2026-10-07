# Week 4 Authentication Lab

A Java RMI mock print server that authenticates every remote operation, supports expiring sessions, and records print jobs without contacting a physical printer. The example uses a protected local credential file containing salted PBKDF2 password hashes, role checks for administrative operations, and TLS-protected RMI.

## Exercise folders

These folder numbers are study groups for the tasks in the handout, which does not number its authentication sections.

- `Exercise 1 - RMI Print Server/`: the remote interface, client, server startup, and mock print operations. Session management is integrated into `PrintServerImpl.java`.
- `Exercise 2 - Passwords and Authentication/`: credential storage, password verification, account creation, and authentication errors.
- `Exercise 3 - Secure Transport/`: TLS settings and the RMI client socket factory.

All folders form one application and retain the Java package `lab.auth`. `build.sh` compiles them together into `out/`; run the commands below from this project directory.

## Requirements

- Java 11 or later (`java`, `javac`, and `keytool`).
- Run the account tool and client from an interactive terminal so passwords are not echoed.

## Build

From this directory:

```sh
./build.sh
```

## Create a TLS key and trust store

The server requires TLS. Generate a local development certificate whose DNS name matches the default client host (`localhost`):

```sh
mkdir -p data
keytool -genkeypair -alias printserver -keyalg RSA -keysize 3072 -validity 365 \
  -storetype PKCS12 -keystore data/server-keystore.p12 \
  -dname "CN=localhost" -ext "SAN=dns:localhost,ip:127.0.0.1"
keytool -exportcert -alias printserver -keystore data/server-keystore.p12 \
  -file data/server-cert.cer
keytool -importcert -noprompt -alias printserver -file data/server-cert.cer \
  -storetype PKCS12 -keystore data/client-truststore.p12
```

`keytool` prompts for store passwords. Protect those passwords and the private keystore. This self-signed certificate setup is for a local exercise; a deployed service should use certificates and key management appropriate to its environment.

## Create users

Create at least one administrator and one ordinary user. The account file is created with owner-only permissions on POSIX systems. The passwords are entered without echo and are not written to the file.

```sh
java -cp out lab.auth.CredentialTool data/users.txt alice USER
java -cp out lab.auth.CredentialTool data/users.txt admin ADMIN
```

The stored record is `username:role:iterations:salt:derivedHash`. Keep `data/users.txt` private and out of source control. The server loads users when it starts; restart it after changing the file.

## Start the server

Set these environment variables in the server terminal, entering the keystore password without echo, then start the RMI registry and service:

```sh
export PRINTSERVER_KEYSTORE="$PWD/data/server-keystore.p12"
read -s PRINTSERVER_KEYSTORE_PASSWORD
export PRINTSERVER_KEYSTORE_PASSWORD
java -cp out lab.auth.PrintServerMain data/users.txt
```

The default ports are 1099 for the registry and 1100 for the service. An alternative invocation is:

```sh
java -cp out lab.auth.PrintServerMain data/users.txt 1099 1100 localhost
```

For a remote client, use a certificate with the server's DNS name in its subject alternative name, set the advertised host to that name, and configure network firewall rules for both RMI ports.

## Connect a client

In another terminal, set the trust store and enter its password without echo, then start the client:

```sh
export PRINTSERVER_TRUSTSTORE="$PWD/data/client-truststore.p12"
read -s PRINTSERVER_TRUSTSTORE_PASSWORD
export PRINTSERVER_TRUSTSTORE_PASSWORD
java -cp out lab.auth.PrintServerClient localhost 1099
```

The client prompts for a username and password, then accepts commands:

```text
print report.pdf Laser-1
queue Laser-1
top Laser-1 1
status Laser-1
readconfig copies
setconfig copies 2
start
stop
restart
logout
login
quit
```

File and printer names cannot contain spaces in this small command loop. `start`, `stop`, `restart`, and `setconfig` require the `ADMIN` role. Other authenticated users may print, inspect queues/status, and read configuration.

## Security design

- The server verifies passwords using PBKDF2-HMAC-SHA-256, an individual random 16-byte salt, and 600,000 iterations. The iteration count is recorded with each credential. The derived verifier is compared with a constant-time comparison; plaintext passwords are not stored. Unknown usernames perform a dummy derivation and receive the same error as an incorrect password.
- The account file is private to the service account. On POSIX systems the server refuses files readable or writable by group/other. On systems without POSIX permissions, use OS ACLs to enforce equivalent access.
- TLS protects the login password and bearer session token in transit. The RMI TLS client factory verifies the certificate chain and matches the server name. Do not expose the service over an untrusted network without correctly managing the certificate and ports.
- Login creates a 256-bit random opaque token. The server stores the associated username and role. Sessions expire after 15 minutes without activity or after 8 hours total. Activity refreshes the idle deadline but not the absolute deadline. Expiration is enforced at the next request; the expired server-side entry is then removed. Restarting the server clears all sessions.
- Every protected method checks the session. Admin operations also check the role. Authentication proves who is using the session; authorization decides which operations that identity may perform.
- Audit output records identity and operation, but never passwords or session tokens. It is demonstration logging, not durable tamper-resistant auditing.

## Scope and limitations

This mock-up keeps print jobs and configuration in memory, and it does not print real files. It is a teaching implementation, not production authentication software. A real deployment should additionally consider rate limiting, account lockout policy, durable audit storage, credential rotation, secure password reset, revocation, deserialization filtering, session cleanup, key rotation, and a reviewed identity/authorization design. Passwords and TLS secrets should be supplied through a protected secret-management mechanism rather than shell history.
