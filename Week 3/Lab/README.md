# Week 3 Lab index

Install the lab dependency with `python -m pip install cryptography`, then run:

Run the scripts from their exercise folders:

```sh
cd "Exercise 1 - Public-Key Encryption"
python public_key_demo.py
cd "../Exercise 2 - Public-Key Authentication"
python mitm_demo.py
cd "../Exercise 2.1-2.3 - TLS Certificates"
python inspect_tls.py www.google.com
```

`Exercise 1 - Public-Key Encryption/public_key_demo.py` creates Bob's 2048-bit RSA key pair and the requested message, encrypted, and decrypted files. The private key is local and is written with owner-only permissions. Encryption uses RSA-OAEP with SHA-256. `Exercise 2 - Public-Key Authentication/mitm_demo.py` simulates Eve replacing Bob's public key, reading Alice's message, and forwarding it encrypted to Bob. The substitution works because a bare public key has no authenticated identity binding.

`Exercise 2.1-2.3 - TLS Certificates/inspect_tls.py` verifies a live TLS connection using the machine's trusted CA store and prints the leaf certificate fields. Certificate dates, SANs, and issuers can change; record the observation time with your lab notes. The TLS handshake's trusted path is validated by the local TLS stack; server certificate chains can differ by client and server configuration. The lecture sample-question answers are in `Week 3/Sample Questions - Lectures 1-3/Answers.md` in the Obsidian Lab vault.
