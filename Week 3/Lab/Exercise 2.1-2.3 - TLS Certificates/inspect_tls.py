"""Inspect the live TLS leaf certificate for a domain (Week 3, Exercise 2)."""

import socket
import ssl
import sys
from datetime import datetime, timezone

from cryptography import x509
from cryptography.hazmat.primitives.asymmetric import ec, ed25519, ed448, rsa


def inspect(domain: str) -> None:
    context = ssl.create_default_context()
    with socket.create_connection((domain, 443), timeout=15) as raw:
        with context.wrap_socket(raw, server_hostname=domain) as tls:
            der = tls.getpeercert(binary_form=True)
            print("TLS verification:", "passed")
            print("TLS version:", tls.version())
    cert = x509.load_der_x509_certificate(der)
    now = datetime.now(timezone.utc)
    print("Observed at (UTC):", now.isoformat(timespec="seconds"))
    print("Domain:", domain)
    print("Subject:", cert.subject.rfc4514_string())
    print("Issuer:", cert.issuer.rfc4514_string())
    print("Valid from:", cert.not_valid_before_utc.isoformat())
    print("Valid until:", cert.not_valid_after_utc.isoformat())
    public_key = cert.public_key()
    if isinstance(public_key, rsa.RSAPublicKey):
        key_algorithm = f"RSA ({public_key.key_size} bits)"
    elif isinstance(public_key, ec.EllipticCurvePublicKey):
        key_algorithm = f"ECDSA public key ({public_key.curve.name})"
    elif isinstance(public_key, ed25519.Ed25519PublicKey):
        key_algorithm = "Ed25519"
    elif isinstance(public_key, ed448.Ed448PublicKey):
        key_algorithm = "Ed448"
    else:
        key_algorithm = type(public_key).__name__
    print("Public-key algorithm:", key_algorithm)
    print("Certificate signature algorithm:", cert.signature_algorithm_oid._name)
    try:
        sans = cert.extensions.get_extension_for_class(x509.SubjectAlternativeName).value
        print("Subject Alternative Names:")
        for name in sans.get_values_for_type(x509.DNSName):
            print(" -", name)
    except x509.ExtensionNotFound:
        print("Subject Alternative Names: none")
    print("Currently within validity period:", cert.not_valid_before_utc <= now <= cert.not_valid_after_utc)


if __name__ == "__main__":
    inspect(sys.argv[1] if len(sys.argv) > 1 else "www.google.com")
