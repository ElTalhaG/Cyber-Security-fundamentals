# Question 9 - Repairing the Replay

## Task

Determine whether any of the proposed changes to the second protocol message repairs the replay in question 8.

## Answer: D

None of A-C changes the fact that B has already accepted the replayed first message. B still sees an old initiation and reuses K(1) in a second run.

A receiver challenge can help if A must return an authenticated response bound to that challenge and B delays acceptance until verifying it. Simply adding a fresh value to B's last outgoing message does not establish a fresh participating A.

A possible practical alternative is to authenticate the initiation and maintain an appropriate replay cache of accepted session identifiers or keys for the relevant lifetime. Such a cache must actually reject duplicates. Another redesign adds a fresh B challenge, a response from A bound to both identities and the session, and an acceptance check. These are design directions, not a claim that an unmodeled revised protocol has been proved secure.

## Assessing the alternatives

- A: a fresh B-generated number in message 2 alone does not prove A's current participation.
- B: names in message 2 can help identity binding but do not make message 1 fresh.
- C: B signing its response authenticates B's output, not a new run of A.
- D is therefore the correct choice for this specific replay goal.

## Remember

A challenge provides authentication only when the relevant peer answers it and the verifier checks the bound response.

## Sources and scope

Original 2025 exam, PDF page 5. This is a reasoned practice solution, not an official marking key.

[DTU OFMC tutorial](https://www2.compute.dtu.dk/~samo/OFMC-tutorial.pdf), PDF page 12. No modified protocol was executed or formally verified.
