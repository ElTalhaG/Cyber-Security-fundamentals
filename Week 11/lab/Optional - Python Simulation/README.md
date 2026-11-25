# Optional Python Simulation

This standard-library-only example creates a simulated identity credential, discloses only requested fields, calculates an age-over-18 claim, and evaluates student and age presentations.

## Run

```bash
python3 -m unittest -v
python3 selective_disclosure.py
```

## Trust boundary

The presentation contains only disclosed attributes. A separate `CredentialEvidence` object represents the result of validating the credential's issuer, authenticity, expiry, and revocation. Its `authenticated` field is a simulation input, not a digital signature. The program does not implement anonymous credentials or zero-knowledge proofs, and it does not prove that a Boolean age claim is cryptographically linked to a signed birth date.
