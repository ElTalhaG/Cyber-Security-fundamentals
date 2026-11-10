# Exercise 2 - Password

## The supplied protocol and its attack

The supplied model sends the payload encrypted directly under the shared password:

~~~text
A -> B: {|Payload|}pw(A,B)
~~~

Because pw(A,B) is guessable, a network attacker can try a candidate password as a decryption key. If the candidate decrypts the ciphertext into the recognizable payload structure, the attacker has an offline test. No interaction with Bob is needed for each guess.

The supplied model marks the password as a guessable secret. Running it with two sessions reports an attack on the guessing goal. In this OFMC model, the intruder can test a candidate against the captured protocol message.

## What extra knowledge is present?

The model includes a trusted public-key infrastructure in addition to the password:

| Knowledge entry | Meaning |
|---|---|
| A knows s and pk(s) | Alice knows the trusted key server/certification authority's identity and public verification key. |
| B knows pk(B) and inv(pk(B)) | Bob owns a public/private key pair. |
| B knows {B,pk(B)}inv(pk(s)) | Bob has a certificate: the trusted signer binds Bob's name to Bob's public key. |
| Both know pw(A,B) | Alice and Bob share the low-entropy password. |
| Both know h | The hash algorithm is public. It is not a secret key. |

Alice does not initially know Bob's public key in the model. She can receive Bob's certificate and validate its signature using pk(s). This represents a common real-world arrangement: a user knows a trust root (for example, a CA root certificate), while a server has a certified public key. The public-key trust relationship is additional security knowledge beyond the password.

## A protocol using the extra knowledge

The fixed model is in [password-fixed.AnB](password-fixed.AnB). It uses Bob's public-key certificate for confidentiality and the shared password for Alice's authentication:

~~~text
B -> A: NB, {B,pk(B)}inv(pk(s))
A -> B: {A,B,NB,Payload,pw(A,B)}pk(B)
~~~

1. Bob creates a fresh challenge NB and sends it with his certificate.
2. Alice checks that the certificate is signed by the trusted signer and binds Bob's name to the public key she will use.
3. Alice encrypts her identity, Bob's identity, Bob's challenge, the payload, and the shared password for Bob.
4. Bob decrypts with inv(pk(B)), checks that the identities and challenge match this run, and verifies the included password before accepting the payload.

The password is now inside randomized public-key encryption rather than serving as the directly observable decryption key for the payload. A passive network observer cannot decrypt the message and compare password guesses against a recognizable plaintext. The fresh NB makes the response specific to Bob's current run, while A, B, and the protocol context bind the message to the intended peers and use.

The public-key encryption must be randomized in a real implementation, such as RSA-OAEP or an appropriately designed hybrid encryption scheme. Raw/deterministic RSA is not suitable. The symbolic AnB model treats public-key encryption ideally and does not model implementation-level padding errors, side channels, or key compromise.

## OFMC model and result

The answer model keeps the course's three goals:

- Bob authenticates Alice on NB and Payload.
- Payload is secret between Alice and Bob.
- pw(A,B) is a guessable secret between Alice and Bob.

The supplied password.AnB reports ATTACK_FOUND for the guesswhat goal with --numSess 2. The repaired model password-fixed.AnB reported:

~~~text
SUMMARY:
  NO_ATTACK_FOUND
GOAL:
  as specified
DETAILS:
  BOUNDED_NUMBER_OF_SESSIONS
~~~

This result means OFMC found no attack for the stated goals within the explored session bound and symbolic model. It is not a proof about all executions or a real implementation. Increasing the bound explores more interleavings but does not remove the abstraction gap.

The repaired model still needs online-guessing protection. An active attacker can submit candidate passwords in attempts to authenticate; rate limiting can constrain those attempts. Modern password-verifier guidance also recommends checking new passwords against blocklists of common or compromised choices. See [NIST SP 800-63B](https://pages.nist.gov/800-63-4/sp800-63b.html).

## Is every password-only protocol vulnerable to guessing?

There is a strong argument **for the claim under an active attacker with unlimited attempts**. If the low-entropy password is the only secret relationship, an attacker can enumerate its likely values. Once the attacker has guessed the correct password, they possess the same credential as the legitimate party. A protocol that relies only on that password cannot distinguish the attacker from Alice or Bob. This prevents a robust guarantee against unbounded guessing.

The literal claim that every transcript permits an **offline** dictionary test is too broad. Whether a captured transcript lets an attacker verify guesses depends on the protocol, encryption randomization, plaintext structure, known values, and available response oracles. Some designs may avoid an immediate offline verifier even though the password remains low entropy. Avoiding an offline test does not make an unlimited online guessing attacker harmless.

A precise conclusion is:

> With only a guessable shared password, a protocol cannot remain secure against an attacker who can make unlimited guesses or who has enough information to verify guesses offline. Protocol design can remove easy offline tests and rate limiting can slow online attempts, but a stronger independent trust mechanism or authenticator is needed to avoid relying on password entropy alone.

This matches the exercise's contrast: the supplied model's extra certified public key is precisely an additional trust relationship that lets the protocol hide the password from passive observers.

## Important assumptions and limitations

- Alice validates Bob's certificate against the correct trust anchor and verifies the identity in the certificate.
- Bob's private key remains secret; compromise of it exposes recorded payloads in this simple static-key design.
- Public-key encryption is randomized and implemented safely.
- The password is checked only after decryption; the application does not expose a detailed error oracle.
- NB is fresh and checked against the current session.
- Bob throttles online password guesses. Throttling does not stop offline guesses from a transcript or a stolen verifier database.
- This AnB model does not represent certificate revocation, transport metadata leakage, cryptographic side channels, or implementation bugs.

