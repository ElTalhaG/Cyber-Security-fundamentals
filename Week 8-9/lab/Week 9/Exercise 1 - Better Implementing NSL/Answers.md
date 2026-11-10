# Exercise 1 - Better Implementing NSL

## What the scenario changes

In the Week 8 implementation, Alice is initialized with Bob's name and public key. The expected peer is fixed for that role object, and the protocol code processes one message at a time. The Week 9 scenario changes the assumptions: Alice can look up peer public keys, a run can time out and lose its state, and a later message may be incorrectly treated as the beginning of a new run.

The dangerous assumption is that a byte string received from the network is self-identifying. Encryption does not tell the application what role or protocol message the ciphertext is intended to represent. If the application decrypts or parses a delayed message in the wrong state, data from an old run can be reinterpreted as fields in a new run.

## Confusion trace

1. Alice starts NSL with Bob. She creates fresh nonce NA and sends the first message encrypted for Bob:
   ~~~text
   A -> B: {NA,A}pk(B)
   ~~~
2. Bob decrypts it, creates fresh NB, and replies:
   ~~~text
   B -> A: {NA,NB,B}pk(A)
   ~~~
3. The intruder withholds Bob's reply until Alice times out and discards the pending-run context.
4. Later, the intruder delivers the old ciphertext to code that mistakenly treats it as a new first message rather than a response to the old run.
5. The decrypted fields can then be parsed under the wrong message schema. Alice may interpret some old nonce bytes as an agent identifier and query a key server for that apparent identity.
6. If that lookup request reveals the parsed fields, the intruder may learn values that were intended to remain secret. Even if the lookup does not leak them, the role/session confusion can lead to incorrect peer assumptions.

The bug is not that public-key cryptography was broken. The application lost the context that gave the ciphertext its meaning.

## How to harden the implementation

Use several checks that address the root cause:

1. **Separate message types and roles.** Give every protocol message an explicit version, protocol identifier, and message-type tag, such as NSL-M1 and NSL-M2. Each handler should accept only the type expected in its current role and state.
2. **Bind the whole context.** Cryptographically protect the sender, intended recipient, protocol name/version, message type, and session challenges along with the payload. Use a canonical, unambiguous encoding with length-prefixed fields.
3. **Keep pending session state.** Accept message 2 only when there is a live Alice run waiting for it, and check its NA against that run's stored nonce and its B field against the expected peer. When a run times out, invalidate it; never reinterpret its late response as a new run.
4. **Make key lookup identity-based and authenticated.** Resolve a canonical peer identity to a certificate/key, validate the certificate chain and name, and bind the selected key to the current protocol session. Do not derive a key-server query from arbitrary bytes parsed out of an unexpected ciphertext.
5. **Protect lookup metadata where needed.** A public-key directory may return public data, but lookup requests can reveal who is communicating and which protocol fields were received. Use an authenticated protected channel for lookups when those details are sensitive. This reduces disclosure; it does not replace message-type and session-state checks.
6. **Use fresh session values.** Generate a new nonce for each run, prevent reuse after timeout, and require the response to contain the challenge for the current run.

A robust conceptual encoding could distinguish the roles explicitly:

~~~text
M1 = version, protocol=NSL, type=M1, sender=A, recipient=B, NA
M2 = version, protocol=NSL, type=M2, sender=B, recipient=A, NA, NB
M3 = version, protocol=NSL, type=M3, sender=A, recipient=B, NB
~~~

Each message's fields must be protected for the intended recipient, and the receiver must check both the cryptographic fields and the expected protocol state. The labels alone are not authentication; they must be included in authenticated/encrypted data and checked.

## Relation to the supplied implementation

The Week 8 Java roles already use one role object per run, retain NA until message 2 arrives, compare it with the response, and receive a fixed peer key at construction. That design prevents the exact key-server/restart scenario in this question because it has no dynamic lookup or generic message dispatcher. The Week 9 attack becomes relevant when a surrounding application adds timeouts, key lookup, message routing, or multiple roles. Those surrounding components must preserve the same session and peer bindings.

A timeout must not erase the distinction between “response to an old run” and “request to start a new run.” Session management should mark old runs closed and reject late responses.

## Security properties and limits

- **Authentication:** message type, peer identity, and run-specific nonce prevent an old response from being accepted in the wrong role or session.
- **Confidentiality:** an authenticated protected lookup channel can stop the lookup request from exposing sensitive parsed fields.
- **Availability:** timeouts and blocking can still be abused to prevent completion; the changes do not prevent denial of service.
- **Assumptions:** certificates are validated against the intended trust root, private keys remain secret, nonces are unpredictable, and message encoding is canonical.

The exercise's current Java implementation was not modified for this answer. The recommended fix is at the design boundary that the handout adds: key resolution, timeouts, and dispatch across protocol states.

