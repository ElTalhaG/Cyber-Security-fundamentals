# Week 5 Authentication and Access Control Lab

This project extends the Week 4 Java RMI mock print server with two interchangeable, externally loaded authorization policies: a per-user ACL and role-based access control (RBAC). Password login, protected credential-file handling, TLS RMI, session tokens, session expiry, mock print operations, and audit logging are retained from Week 4.

## Requirements and build

Use Java 11 or later (`java`, `javac`, and `keytool`). From this directory:

```sh
mkdir -p out
javac -d out src/lab/auth/*.java
```

## Prepare local TLS

The server and registry use TLS. For local practice, create a self-signed certificate for `localhost` and import it into a client trust store:

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

Choose passwords when `keytool` prompts. This certificate is for local practice; use suitable certificates and key management for a real service.

## Create accounts

Create a password record for every account in the selected policy file. For the example, use the `USER` metadata value for each account: Week 5 authorization is decided by the external ACL or RBAC policy, not this legacy Week 4 field. Passwords are entered without echo and must be at least 12 characters.

```sh
java -cp out lab.auth.CredentialTool data/users.txt alice USER
java -cp out lab.auth.CredentialTool data/users.txt bob USER
java -cp out lab.auth.CredentialTool data/users.txt cecilia USER
java -cp out lab.auth.CredentialTool data/users.txt david USER
java -cp out lab.auth.CredentialTool data/users.txt erica USER
java -cp out lab.auth.CredentialTool data/users.txt fred USER
java -cp out lab.auth.CredentialTool data/users.txt george USER
```

The private credential file stores per-user PBKDF2-HMAC-SHA-256 verifiers, not passwords. Its records are `username:metadata:iterations:salt:derivedHash`; keep the file owner-only and out of source control. For the organizational change, add Henry and Ida, remove Bob's record, and restart the server. Use the same account passwords in both policy experiments so the only changing variable is authorization.

## Run either authorization prototype

Set the keystore environment variables in the server terminal and choose an external policy file:

```sh
export PRINTSERVER_KEYSTORE="$PWD/data/server-keystore.p12"
read -s PRINTSERVER_KEYSTORE_PASSWORD
export PRINTSERVER_KEYSTORE_PASSWORD
java -cp out lab.auth.PrintServerMain data/users.txt acl policy/acl-initial.properties
```

For RBAC, stop the first server and run:

```sh
java -cp out lab.auth.PrintServerMain data/users.txt rbac policy/rbac-initial.properties
```

For the new organization, use `policy/acl-reorganization.properties` or `policy/rbac-reorganization.properties`. The policy mode and selected file are shown in the server startup message. Policy files are loaded at startup; restart after changing a file.

In another terminal, set the client trust store and connect:

```sh
export PRINTSERVER_TRUSTSTORE="$PWD/data/client-truststore.p12"
read -s PRINTSERVER_TRUSTSTORE_PASSWORD
export PRINTSERVER_TRUSTSTORE_PASSWORD
java -cp out lab.auth.PrintServerClient localhost 1099
```

The client supports `print`, `queue`, `top`, `start`, `stop`, `restart`, `status`, `readconfig`, and `setconfig`; the server checks the selected policy for every operation.

## External policy formats

ACL files are Java properties: each username maps directly to a comma-separated list of operation names; `*` grants every operation. The server denies users absent from the file and rejects unknown operations.

RBAC files use `user.<username>=<role>[,<role>...]`, `role.<role>=<operation>[,...]`, and `inherits.<senior-role>=<junior-role>[,...]`. Senior roles inherit the junior roles' permissions. The loader rejects unknown users/roles/operations and role-hierarchy cycles. Users absent from the user-assignment entries are denied.

## Files

- `policy/acl-initial.properties` and `policy/rbac-initial.properties` implement the original roster.
- `policy/acl-reorganization.properties` and `policy/rbac-reorganization.properties` capture Bob's departure, George's promotion to service technician, and the additions of Henry and Ida.
- `src/lab/auth/AccessPolicy.java` loads and enforces both policy abstractions.

The server remains a teaching mock: queues and configuration are in memory, no real printer is used, and no named file is opened. It does not implement account lockout, password reset, persistent audit storage, or production RMI deserialization hardening.
