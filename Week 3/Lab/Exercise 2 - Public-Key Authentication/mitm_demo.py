"""Simulate Eve replacing Bob's public key (Week 3, Exercise 2)."""

from pathlib import Path

from cryptography.hazmat.primitives import hashes, serialization
from cryptography.hazmat.primitives.asymmetric import padding, rsa

HERE = Path(__file__).resolve().parent
BOB_FILES = HERE.parent / "Exercise 1 - Public-Key Encryption"


def oaep():
    return padding.OAEP(
        mgf=padding.MGF1(algorithm=hashes.SHA256()),
        algorithm=hashes.SHA256(),
        label=None,
    )


def main() -> None:
    private_key = serialization.load_pem_private_key(
        (BOB_FILES / "bob_private.pem").read_bytes(), password=None
    )
    bob_public = serialization.load_pem_public_key(
        (BOB_FILES / "bob_public.pem").read_bytes()
    )
    message = (BOB_FILES / "alice_message.txt").read_bytes()

    eve_private = rsa.generate_private_key(public_exponent=65537, key_size=2048)
    # Alice is tricked into encrypting to Eve's substituted public key.
    intercepted = eve_private.public_key().encrypt(message, oaep())
    read_by_eve = eve_private.decrypt(intercepted, oaep())

    # Eve can forward a new ciphertext under Bob's authentic public key.
    forwarded = bob_public.encrypt(read_by_eve, oaep())
    read_by_bob = private_key.decrypt(forwarded, oaep())

    print("Eve recovers Alice's message:", read_by_eve == message)
    print("Bob decrypts Eve's forwarded message:", read_by_bob == message)
    print("Recovered by Eve:", read_by_eve.decode("utf-8"))
    print("The attack works because the public key's identity was not authenticated.")


if __name__ == "__main__":
    main()
