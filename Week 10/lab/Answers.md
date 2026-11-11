# Week 10 - Privacy Exercises: Answers

The worksheet has four parts: a privacy-friendly sick note, an e-voting multiple-choice question, onion routing, and Schnorr zero-knowledge proofs. These are reasoned solutions based on the handout; no official answer key was supplied.

## 1. Privacy-friendly electronic sick note

### Given protocol

The doctor `C` gives patient `A` a sick note, which `A` forwards to employer `B` over secure channels. The signed content includes the note type, patient identity, sickness dates, and a certificate for the doctor's signing key:

~~~text
C -> A -> B:
  {sicknote, A, Dates, {keycert, SigK}inv(pk(s))}inv(SigK)
~~~

The authority `s` certifies the doctor's public key, and the doctor signs the note. The employer needs to verify both that a legitimate doctor signed the note and that the note names the correct employee and dates.

### Property check

| Desired property | Holds in the given protocol? | Reason |
|---|---|---|
| `B` can verify that a legitimate doctor issued the note for `A` and `Dates`. | **Yes**, assuming `B` validates `s`'s certificate and verifies the doctor's signature. | The certified key identifies an authorized doctor, and the doctor's signature covers the note, patient, and dates. A secure channel alone would not provide this proof; the signatures and certificate do. |
| `B` learns no more than that some legitimate doctor issued the note, and does not learn the doctor's identity. | **No.** | The certificate reveals a doctor-specific signing key and its association with a doctor. The employer can identify the signer if the certificate maps `SigK` to `C`. |
| `B` cannot link two notes to the same doctor. | **No.** | Both notes verify under the same stable public key/certificate. Even without a real-world name, the repeated key is a linkable identifier. |
| `s` learns only the identity of doctor `C`, not patient `A` or employer `B`. | **Yes for the protocol as written.** | `s` issues the doctor's certificate when the practice opens; it is not in the sick-note delivery path. This assumes the doctor does not send notes or transaction metadata to `s`. |
| A court can identify a dishonest doctor from a fraudulent note. | **Yes for the protocol as written.** | The note carries the doctor's certificate and a signature verifiable under that doctor's key, so the signer is identifiable. |

### A design that preserves privacy and accountability

Use a privacy-preserving credential or group-signature scheme. The authority certifies that a doctor is licensed. For each sick note, the doctor creates a randomized proof that:

1. the signer holds a valid doctor credential;
2. the credential is valid under the trusted issuer;
3. the signed statement covers `sicknote`, `A`, and `Dates`; and
4. the presentation does not disclose a stable doctor-specific key or identifier.

The employer verifies the proof and the patient/date binding, but does not learn which doctor signed it. Randomized presentations make separate notes unlinkable. The proof must bind the exact note contents so it cannot be copied to a different patient or date range.

For accountability, include an identity escrow encrypted to a designated opening authority, or use an accountable group signature. The doctor proves that the escrowed value is correctly formed and bound to the valid doctor credential. Only a court-authorized opening reveals the doctor. To meet the requirement on `s`, the opening process should reveal only `C` to `s`; the patient and employer fields should not be included in the escrow value or sent to the authority. Routine credential issuance may let `s` know which doctors it licensed, but `s` need not receive sick-note transactions.

This design relies on careful separation of roles: the employer verifies eligibility, the issuer certifies credentials, and the opening authority learns an identity only under the defined legal process. The cryptographic construction must support unlinkable presentations and selective opening; an ordinary certificate or a stable public key does not provide these properties.

## 2. E-voting: identify the incorrect statement

The message is:

~~~text
{vote, {vote, Vote, Random}pk(Counter)}inv(pk(Voter))
~~~

The voter signs an encryption of the candidate and a fresh random value for the counter. Assume public-key encryption is secure and voter signatures are unforgeable.

**Incorrect statement: “The Random value prevents that the intruder can replay votes and thereby change the election result.”** Randomness does not reject a repeated message. An attacker can replay the same signed ciphertext, including its original random value. The counter needs explicit duplicate/replay handling, such as checking that each voter is counted once and rejecting repeated ballot identifiers. The random value can help identify an exact duplicate only if the counter records and checks it; freshness alone does not enforce that rule.

| Statement | Correct? | Reason |
|---|---|---|
| The random value prevents a guessing attack on votes. | **Yes, for a passive observer of this message.** | The vote is encrypted, and the fresh hidden random value prevents testing a candidate by comparing against one predictable ciphertext. This assumes secure randomized encryption. The counter can decrypt the vote, and the statement does not claim secrecy from the counter. |
| The random value prevents replay and result manipulation. | **No. This is the incorrect statement.** | A replay repeats the entire signed ciphertext and its random value. The protocol as written gives no duplicate-rejection rule. |
| Dishonest voters can cast the same vote as honest Alice without knowing Alice's choice. | **Yes.** | The encrypted inner ballot is visible as part of the signed message even though its contents are opaque. Dishonest voters can copy Alice's ciphertext and sign that same ciphertext with their own keys. The counter decrypts the same choice for each copied ciphertext. |
| A dishonest counter cannot fake a vote signed by an honest voter. | **Yes**, assuming signature unforgeability and uncompromised voter keys. | The counter does not know the voter's private signing key, so it cannot create a new valid voter signature. This does not prevent the counter from miscounting, suppressing, or replaying ballots. |

**Answer: the second statement is incorrect.** Randomness helps hide the vote from observers; it is not a replay-prevention mechanism. The signature authenticates who submitted a ciphertext, but it does not prove the counter included the ballot correctly or counted it only once.

## 3. Onion routing: message sent by `N2`

For the route `N1 -> N2 -> N7 -> N8 -> N3`, `N2` forwards the next-hop identity `N7` and the ciphertext layers intended for `N7`, `N8`, and destination `N3`:

$$
N_7,\{N_8,\{N_3,\{M\}_{pk(N_3)}\}_{pk(N_8)}\}_{pk(N_7)}
$$

**Select the first option.** `N7` removes its layer and learns that it should forward to `N8`; it cannot read the remaining route or message. `N8` similarly learns to forward to `N3`. The destination removes its own layer to recover `M`.

The route field for each next hop is protected inside the layer intended for that relay. The answer assumes the worksheet's symbolic public-key encryption notation and does not model timing or traffic-correlation analysis.

## 4. Schnorr: cheating when Victor always sends `C = 1`

In Schnorr, Peggy's public value is `Z = g^X`. She commits with `T = g^K`, receives challenge `C`, and responds with `R = K + X·C`. Victor checks:

$$
g^R \equiv T \cdot Z^C
$$

If Victor always sends `C = 1`, Pamela can pass without knowing `X`:

1. Choose a random `K`.
2. Set the commitment to `T = g^K / Z` (equivalently, `g^K Z^{-1}` in the group).
3. Respond with `R = K`.

Then:

$$
g^R = g^K = (g^K/Z)Z = T \cdot Z
$$

The verifier accepts. **Select the fourth option:** generate random `K`, use `g^K/Z` as the commitment, and use `K` as the response. The challenge must be chosen unpredictably after the commitment. If it were always 0, a cheater could instead commit to `g^K` and answer `K`.

## Compact answer key

1. Sick note: the original protocol authenticates the doctor and note details, but fails doctor anonymity and unlinkability. `s` need not learn patient/employer information because it is outside the delivery path. Court identification is possible. Use anonymous credentials or group signatures with controlled identity escrow.
2. E-voting: **the replay-prevention statement is incorrect** (statement 2).
3. Onion routing: **option 1**.
4. Schnorr: **option 4** (`T = g^K/Z`, `R = K`).

## Sources

- Course exercise sheet: `Week 10/lab/privsl.pdf`, pages 1-3.
- Course slides: `Week 10/Slides/privacy.pdf`, especially pages 9-12 (privacy and unlinkability), pages 14-15 (onion routing), and pages 16-18 (zero-knowledge and Schnorr).
