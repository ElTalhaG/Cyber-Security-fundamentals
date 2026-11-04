# Exercise 2 - Implementing Needham-Schroeder-Lowe

This is a small Java implementation of the message-processing part of the Lowe-fixed NSL protocol in `nsl.AnB`. It follows the handout's scope: Alice and Bob process one incoming message and return the next outgoing message. It does not add sockets, a network, or session management.

## What each role does

| Protocol step | Java method | Work performed |
|---|---|---|
| `A -> B: {NA,A}pk(B)` | `AliceRole.step1()` | Generate a fresh 32-byte `NA`, append Alice's UTF-8 name, and encrypt for Bob. |
| `B -> A: {NA,NB,B}pk(A)` | `BobRole.step1(message1)` | Decrypt with Bob's private key, check Alice's name, generate fresh `NB`, append Bob's name, and encrypt for Alice. |
| `A -> B: {NB}pk(B)` | `AliceRole.step2_3(message2)` | Decrypt with Alice's private key, check the original `NA` and expected Bob name, then encrypt `NB` for Bob. |
| Bob accepts | `BobRole.step3(message3)` | Decrypt with Bob's private key and check that the returned value equals Bob's fresh `NB`. |

The message layout is exactly the handout's fixed-length nonce followed by the name. Names are UTF-8, nonempty, and limited to 64 bytes. One role object represents one protocol run; create new role objects for another run.

## Run it in VS Code

The code uses the Java Cryptography Architecture included with the JDK, so this project has no external library dependency. It uses RSA-OAEP with SHA-256 and a 2048-bit key pair for this classroom demonstration.

In the VS Code terminal, run:

```bash
cd "/Users/talha/DTU/3 semester/CyberSec fundamentals 02339/Week 8-9/lab/Exercise 2 - Implement NSL"
./run.sh
```

Expected output:

```text
NSL exchange completed: Alice and Bob accepted the expected nonces and identities.
No network was used; each returned byte array was passed directly to the next role.
```

The script compiles with Java 17 language compatibility and places generated files under the ignored `build/` directory.

## Important boundary

This demonstrates message parsing, nonce checks, key use, and role interoperability. It is not a production protocol implementation: it omits certificate validation, network framing, persistent session management, and a formal link between key pairs and real-world identities. OAEP also has a maximum plaintext size; the short exercise messages fit within the chosen 2048-bit RSA parameters.
