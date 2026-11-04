# Exercise 1 - Init Protocol

## Goal

Explain the secrecy attack in the supplied `init.AnB`, repair the protocol, and check the repaired model with OFMC.

## How to run the supplied model

Open a terminal in VS Code and run:

```bash
cd "/Users/talha/DTU/3 semester/CyberSec fundamentals 02339/Week 8-9/lab"
./ofmc-tool/ofmc2024.2/executables/mac/ofmc --numSess 2 init.AnB
```

The supplied Mac executable is already included in the course folder. OFMC is a command-line checker, so its results appear in this terminal.

The original model reports `ATTACK_FOUND`, the goal `secrets`, and `secret leaked: Ktemp(2)`.

## Reading the attack trace

OFMC writes the attack as message transfers. `i` is the intruder. Names such as `x20` and `x25` stand for agents selected for the protocol roles; `(x20,1)` means that agent's first protocol session.

1. A sends `{NA,A}` signed with A's private key. A signature prevents the intruder from changing the signed contents, but the contents are readable to anyone with A's public key. The intruder learns `NA` and A's name.
2. The intruder starts a server session as itself and sends `{NA,i}` signed with the intruder's own private key. The server accepts a fresh request from `i` that reuses A's nonce.
3. The server creates `Ktemp` for that session and encrypts the response to the intruder's public key. The intruder can decrypt its own response and learn the key.
4. The intruder forwards the response material to A. The server's signature covers the protocol tag and key, but not the identity the key was issued for. A therefore cannot distinguish a key issued for the intruder from one issued for A.

The weakness is an identity-binding error: the authenticated key is not tied to its intended participant. The protocol tag identifies the protocol, but does not identify the key's recipient.

## Repair

The server must include the intended recipient identity inside the data it signs. In the fixed model, the server signs `tag, A, Ktemp` rather than only `tag, Ktemp`:

```text
{tag,{tag,A,Ktemp}inv(pk(S))}pk(A)
```

Alice checks that the identity inside the server-signed ticket is her own identity. In the attack, the server creates a ticket naming the intruder. The intruder cannot change that signed identity to A, so A rejects the ticket instead of accepting the intruder's key as her own.

The complete repaired model is [`init-fixed.AnB`](init-fixed.AnB). The supplied original remains unchanged at [`../init.AnB`](../init.AnB).

## Check the repair

From the `Week 8-9/lab` folder, run:

```bash
./ofmc-tool/ofmc2024.2/executables/mac/ofmc --numSess 2 "Exercise 1 - Init Protocol/init-fixed.AnB"
```

Expected result:

```text
SUMMARY:
  NO_ATTACK_FOUND
DETAILS:
  BOUNDED_NUMBER_OF_SESSIONS
```

This says OFMC found no attack within the checked bound of two sessions. It is not a proof for every possible number of sessions or every implementation detail.

## Notation used here

| Notation | Meaning |
|---|---|
| `pk(A)` | A's public key |
| `inv(pk(A))` | A's private key; using it in this model represents A signing |
| `{M}pk(A)` | Message `M` encrypted so A can decrypt it |
| `{M}inv(pk(A))` | A's signature over `M`; the signed contents are not confidential |
| `\{|M|\}Ktemp` | Message `M` encrypted under symmetric key `Ktemp` |
| `NA` | A fresh nonce used to associate messages with a protocol run |

## Takeaway

Whenever a protocol authenticates a key, the authenticated data should also identify the participants and purpose the key is for. A valid signature alone does not prove that a key belongs to the session in which it is later used.

## Source

The task and starter attack are from [`psl-02339.pdf`](../psl-02339.pdf), page 1. The symbolic notation is introduced in [`protocol_sec_1.pdf`](../../slides/protocol_sec_1.pdf), pages 6-7 and 15.
