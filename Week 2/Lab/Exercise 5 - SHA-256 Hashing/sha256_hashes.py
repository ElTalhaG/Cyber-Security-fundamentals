"""SHA-256 examples and output-size facts (Week 2, Exercise 5)."""

import hashlib

STRINGS = ["password", "Password", "password1", "password2"]


def sha256_hex(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


if __name__ == "__main__":
    for text in STRINGS:
        digest = hashlib.sha256(text.encode("utf-8")).digest()
        print(f"{text!r}: {digest.hex()}")
    print("SHA-256 output size:")
    print("  bits:", hashlib.sha256().digest_size * 8)
    print("  bytes:", hashlib.sha256().digest_size)
    print("  hexadecimal characters:", len(hashlib.sha256().hexdigest()))
