# Question 17 - End-to-End Encryption

## Task

Identify the best description of end-to-end encryption in the lecture model.

## Answer: B

B describes content encrypted by the sending endpoint and kept encrypted until the intended receiving endpoint decrypts it. In the lecture's model this is application-layer protection.

Intermediate routers can forward the communication without learning the protected plaintext. Some routing metadata remains visible, and endpoint compromise can expose content before encryption or after decryption. E2E protection does not imply that all headers, timing, or communicating identities are hidden.

## Assessing the alternatives

- A describes hop-by-hop/link encryption with decryption at routers.
- C assigns the protection to intermediate devices rather than the communicating endpoints.
- D allows the network provider to read content, contrary to the intended endpoint-only confidentiality model.

## Remember

Follow where the content becomes plaintext. Routers decrypting the content at every hop is not end-to-end confidentiality.

## Sources and scope

Original 2025 exam, PDF page 9. This is a reasoned practice solution, not an official marking key.
