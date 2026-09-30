"""RSA confidentiality and public-key substitution demonstration for Week 3."""

from pathlib import Path
import os

from cryptography.hazmat.primitives import hashes, serialization
from cryptography.hazmat.primitives.asymmetric import padding, rsa

HERE = Path(__file__).resolve().parent
PRIVATE = HERE / "bob_private.pem"
PUBLIC = HERE / "bob_public.pem"
MESSAGE = HERE / "alice_message.txt"
CIPHERTEXT = HERE / "encrypted_message.bin"
RECOVERED = HERE / "decrypted_message.txt"


def generate_bob_keypair() -> None:
    private_key = rsa.generate_private_key(public_exponent=65537, key_size=2048)
    PRIVATE.write_bytes(private_key.private_bytes(
        serialization.Encoding.PEM,
        serialization.PrivateFormat.PKCS8,
        serialization.NoEncryption(),
    ))
    os.chmod(PRIVATE, 0o600)
    PUBLIC.write_bytes(private_key.public_key().public_bytes(
        serialization.Encoding.PEM,
        serialization.PublicFormat.SubjectPublicKeyInfo,
    ))


def encrypt_message() -> None:
    public_key = serialization.load_pem_public_key(PUBLIC.read_bytes())
    CIPHERTEXT.write_bytes(public_key.encrypt(
        MESSAGE.read_bytes(),
        padding.OAEP(mgf=padding.MGF1(algorithm=hashes.SHA256()),
                     algorithm=hashes.SHA256(), label=None),
    ))


def decrypt_message() -> None:
    private_key = serialization.load_pem_private_key(PRIVATE.read_bytes(), password=None)
    RECOVERED.write_bytes(private_key.decrypt(
        CIPHERTEXT.read_bytes(),
        padding.OAEP(mgf=padding.MGF1(algorithm=hashes.SHA256()),
                     algorithm=hashes.SHA256(), label=None),
    ))


def main() -> None:
    if not PRIVATE.exists() or not PUBLIC.exists():
        generate_bob_keypair()
    if not MESSAGE.exists():
        MESSAGE.write_text("Hello Bob, the meeting is at 14:00. Alice", encoding="utf-8")
    encrypt_message()
    decrypt_message()
    original = MESSAGE.read_bytes()
    recovered = RECOVERED.read_bytes()
    print("RSA key size:", serialization.load_pem_private_key(
        PRIVATE.read_bytes(), password=None).key_size, "bits")
    print("Original and decrypted files identical:", original == recovered)
    print("Decrypted message:", recovered.decode("utf-8"))
    print("Files written in:", HERE)


if __name__ == "__main__":
    main()
