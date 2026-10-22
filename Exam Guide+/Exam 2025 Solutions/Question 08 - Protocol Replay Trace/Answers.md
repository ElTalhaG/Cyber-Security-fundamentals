# Question 8 - Protocol Replay Trace

## Task

Identify the goal violated by the supplied OFMC attack trace.

## Answer: B

The violated goal is **B authenticates A on K**: the responder accepts the same initiation and key in two sessions even though A initiated only one matching run.

Let E denote the first encrypted message containing A, B, K(1), and their shared secret. In the trace, x502 plays A, x501 plays B, and i is the intruder:

```text
A sends E once.
i forwards E to B in session 1.
B responds using K(1).
i replays the identical E to B in session 2.
B responds again using the same K(1).
```

B's two completed runs cannot map to two distinct originating runs of A. This violates strong/injective authentication, which includes replay protection. Encrypting the initiation protects its contents but does not stop the attacker from copying it.

The attacker is not shown decrypting E, learning K(1), learning Msg, or guessing the shared secret. There is also no shown completion by A on a substituted Msg. Identify the goal violated by this trace, rather than an unrelated attack one might investigate in another execution.

## Assessing the alternatives

- A: no plaintext Msg disclosure is shown.
- C: the trace does not show A being deceived into accepting a forged response on Msg.
- D: the trace reuses ciphertext without a displayed password guess or secret recovery.

## Remember

Two responder acceptances for one initiator run demonstrate a replay violation of injective agreement.

## Sources and scope

Original 2025 exam, PDF page 4-5. This is a reasoned practice solution, not an official marking key.

[DTU OFMC tutorial](https://www2.compute.dtu.dk/~samo/OFMC-tutorial.pdf), PDF pages 10-12, explains strong authentication and replay. This answer manually analyzes the supplied trace; OFMC was not run.
