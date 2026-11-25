# Week 11 Lab - Selective Disclosure

All four written exercises from the supplied worksheet are answered. The handout also recommends a small Python simulation; that implementation and its test scenarios are included here.

## Answers

1. [Understanding digital identity credentials](Exercise%201%20-%20Identity%20Attributes/Answers.md)
2. [Selective disclosure for a student discount](Exercise%202%20-%20Student%20Status/Answers.md)
3. [Age verification without date of birth](Exercise%203%20-%20Age%20Verification/Answers.md)
4. [Evaluating student presentations](Exercise%204%20-%20Basic%20Verifier/Answers.md)

## Recommended Python simulation

See [Optional Python Simulation](Optional%20-%20Python%20Simulation/README.md). It generates minimal presentations and simulates student and age checks, including missing, invalid, unauthenticated, expired, and revoked evidence. Run it with:

```bash
cd "Week 11/lab/Optional - Python Simulation"
python3 -m unittest -v
python3 selective_disclosure.py
```

The simulator deliberately treats issuer authentication as separate validation evidence. It does not claim that plain JSON proves who issued a credential or that a presentation is a cryptographic zero-knowledge proof.

## Source

The tasks are in [`identity_Mng_lab.pdf`](../identity_Mng_lab.pdf). Related identity and anonymous-credential concepts are in [`Identity Management slides.pdf`](../Identity%20Management%20slides.pdf).
