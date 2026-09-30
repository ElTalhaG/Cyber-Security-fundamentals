"""Byte-wise XOR and a correctly sized, cryptographically random one-time pad."""

import base64
import secrets


def xor_bytes(data: bytes, key: bytes) -> bytes:
    if not key:
        raise ValueError("key cannot be empty")
    return bytes(value ^ key[index % len(key)] for index, value in enumerate(data))


def make_otp_key(length: int) -> bytes:
    if length < 0:
        raise ValueError("length cannot be negative")
    return secrets.token_bytes(length)


if __name__ == "__main__":
    import sys
    if "--interactive" in sys.argv:
        message = input("Plaintext: ")
        key_text = input("XOR key (text): ")
        ciphertext = xor_bytes(message.encode("utf-8"), key_text.encode("utf-8"))
        print("Ciphertext (base64):", base64.b64encode(ciphertext).decode("ascii"))
        recovered = xor_bytes(ciphertext, key_text.encode("utf-8")).decode("utf-8")
        print("Decrypted:", recovered)

    # Repeating-key XOR examples: one-byte, short repeating, and longer-than-text key.
    samples = [("HELLO", b"K"), ("HELLO", b"ICE"), ("HI", b"longer-than-text")]
    for sample, sample_key in samples:
        encrypted = xor_bytes(sample.encode("utf-8"), sample_key)
        decoded = xor_bytes(encrypted, sample_key).decode("utf-8")
        print(f"XOR sample {sample!r}, key={sample_key!r}, round-trip={decoded == sample}")

    message = "This is a simple secret message"
    plaintext = message.encode("utf-8")
    key = make_otp_key(len(plaintext))
    ciphertext = xor_bytes(plaintext, key)
    recovered = xor_bytes(ciphertext, key).decode("utf-8")
    print("Plaintext:", message)
    print("Key (base64; keep secret and never reuse):", base64.b64encode(key).decode("ascii"))
    print("Ciphertext (base64):", base64.b64encode(ciphertext).decode("ascii"))
    print("Decrypted:", recovered)
