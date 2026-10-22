# Question 7 - TLS Authentication

## Task

Choose the correct TLS statement among claims about pre-known public keys and authentication of Diffie-Hellman shares.

## Answer: D, in the authenticated Diffie-Hellman model

D matches the usual server-authenticated Diffie-Hellman TLS handshake. The client must authenticate the server side of the exchange so that an attacker cannot substitute its own share and impersonate the server.

For example, in certificate-based TLS 1.3, the server signs the handshake transcript using CertificateVerify. That transcript binds the exchange, including the key shares, to the authenticated signing key. This is more precise than saying the server simply signs one isolated DH value.

The client can learn the server certificate during the handshake and validate it against existing trust anchors and the expected server identity. It need not know that particular server public key in advance.

**Scope caveat:** TLS supports different modes, including PSK-only TLS 1.3, which has no DH share. D is the answer under the lecture's authenticated-DH model, not a literal claim about every possible TLS handshake. Optional client certificate authentication also does not mean all TLS clients must have a pre-established identity.

## Assessing the alternatives

- A and B unnecessarily require the specific peer public keys in advance.
- C, interpreted as requiring an authenticated client identity, is not mandatory in ordinary server-authenticated TLS. The transcript is protected, but that is distinct from requiring client certificates.

## Remember

Authenticate the key exchange, and distinguish a trusted CA key from already knowing the individual server key.

## Sources and scope

Original 2025 exam, PDF page 4. This is a reasoned practice solution, not an official marking key.

[RFC 8446, §2](https://www.rfc-editor.org/rfc/rfc8446.html#section-2) describes TLS 1.3 modes; [§4.4.3](https://www.rfc-editor.org/rfc/rfc8446.html#section-4.4.3) specifies CertificateVerify.
