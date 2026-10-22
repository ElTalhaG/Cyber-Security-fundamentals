# Question 3 - Replay and Freshness

## Task

A badge sends the same encrypted token each time; captured tokens can be replayed to unlock a door. Identify the main flaw.

## Answer: C

The server has no effective freshness check. Encryption can hide a token without stopping someone from retransmitting the exact captured bytes. A static encrypted token therefore behaves like a reusable bearer credential.

A stronger design can use a fresh server challenge and have the badge authenticate that challenge with a protected key. The server must verify that the response is bound to its current outstanding challenge and reject reuse. A checked counter or carefully designed time-based mechanism can also provide freshness. Merely placing an unchecked nonce inside a token does not prevent replay.

## Assessing the alternatives

- A: symmetric keys can support secure challenge-response authentication.
- B: asymmetric cryptography does not automatically give freshness; a static signed token can also be replayed.
- D: replacing a static ciphertext with a static hash leaves a replayable value.

## Remember

Confidentiality is different from freshness. The verifier must check something unique to the current authentication attempt.

## Sources and scope

Original 2025 exam, PDF page 2. This is a reasoned practice solution, not an official marking key.
