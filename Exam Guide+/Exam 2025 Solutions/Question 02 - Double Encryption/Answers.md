# Question 2 - Double Encryption

## Task

Assess the proposal to encrypt file contents with AES-128 and then encrypt the entire ciphertext using RSA-2048.

## Answer: B, intended; wording overstates the conclusion

B is the practical answer the question appears to seek: applying RSA directly to bulk ciphertext is inefficient and does not automatically double security. A standard hybrid design encrypts file data using authenticated symmetric encryption and protects a small symmetric key using a suitable public-key mechanism.

However, “adds no meaningful security” is not a universal cryptographic theorem. Independent, correctly composed encryption layers can provide additional protection under some threat models. The useful criticism here is the unspecified composition and inefficient use of RSA for large data.

RSA also has a strict message-size limit for one operation. With RSA-OAEP, a 2048-bit modulus and SHA-256 allow at most 256 - 2×32 - 2 = **190 bytes** of input. A file-sized AES ciphertext cannot normally be put into a single such operation. Splitting a file into ad hoc RSA chunks is not a substitute for a defined secure file-encryption construction.

## Assessing the alternatives

- A is too absolute: RSA can encrypt a suitably short byte string even if those bytes are AES ciphertext.
- C is not justified: AES-128 is not made unsuitable simply by being a symmetric cipher with a 128-bit key.
- D is false: layered encryption is not always weaker.

## Remember

Use symmetric encryption for bulk data and public-key cryptography for an appropriate small key-establishment or key-protection task.

## Sources and scope

Original 2025 exam, PDF page 2. This is a reasoned practice solution, not an official marking key.

[RFC 8017, §7.1.1](https://www.rfc-editor.org/rfc/rfc8017.html#section-7.1.1) specifies the RSA-OAEP size bound. The 190-byte example assumes SHA-256; the exam does not specify padding or a hash.
