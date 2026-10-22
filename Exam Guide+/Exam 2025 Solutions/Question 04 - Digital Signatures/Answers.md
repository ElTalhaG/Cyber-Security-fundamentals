# Question 4 - Digital Signatures

## Task

Choose the correct statement about digital signatures.

## Answer: C, with a trusted identity-to-key binding

C is the intended answer: a signature supports authentication of the signer and integrity of the signed message. Verification demonstrates consistency with the corresponding private key, assuming a secure signature scheme and protected key.

Attributing that key to a real sender also needs a trusted identity-to-public-key binding, such as a validated certificate or an independently verified key. A valid signature under an arbitrary attacker-supplied key does not establish the identity the attacker claims.

## Assessing the alternatives

- A: signing is not generally encrypting the whole message; signature and encryption algorithms serve different purposes.
- B: a signature does not by itself conceal the message.
- D: signatures do not prevent every attack, including endpoint compromise or replay of an otherwise valid signed message.

## Remember

A signature binds protected message content to a signing key; identity depends on how that public key is trusted.

## Sources and scope

Original 2025 exam, PDF page 3. This is a reasoned practice solution, not an official marking key.
