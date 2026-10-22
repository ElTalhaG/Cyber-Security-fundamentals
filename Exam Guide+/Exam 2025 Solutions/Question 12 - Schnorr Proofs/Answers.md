# Question 12 - Schnorr Proofs

## Task

Assess commitment reuse and the claimed 2^(-N) success probability in Schnorr identification.

## Answer: A; C is also true under binary-challenge assumptions

A describes the central commitment-reuse failure: two valid responses using the same commitment and different challenges let the verifier recover the secret.

For a prime-order subgroup of order q, use the response convention s = r + cX mod q and commitment T = g^r mod p. With the same r and distinct challenges c1 and c2:

$$
s_1=r+c_1X\pmod q,\qquad s_2=r+c_2X\pmod q
$$

Subtracting gives:

$$
X=(s_1-s_2)(c_1-c_2)^{-1}\pmod q.
$$

The RFC uses the equivalent minus-sign response convention. Distinct challenges are essential: repeating an identical transcript supplies no second independent equation.

**C needs a missing assumption.** With independent uniform one-bit challenges and fresh commitments, a prover without the witness can prepare for one of the two challenges, giving the familiar 2^(-N) soundness bound over N rounds. With a t-bit challenge the corresponding challenge-guessing bound is 2^(-tN), not generally 2^(-N). These are cryptographic bounds under the protocol's security assumptions, not an unconditional exact probability for every attacker.

**Practice conclusion:** select A as the broadly supported answer, but flag that C also describes the common binary-challenge teaching version. The paper refers to a lecture variant not included here, so a unique official choice cannot be certified.

## Assessing the alternatives

- B: Z is already public; learning it is not the intended achievement.
- C: conditionally valid for the binary independent-challenge version; not a universal numerical claim about every Schnorr variant.
- D: the soundness error is a cheating-prover probability, not the probability that an honest verifier learns X from correctly generated fresh transcripts.

## Remember

Never reuse Schnorr commitment randomness across different challenges. State the challenge space before quoting a soundness bound.

## Sources and scope

Original 2025 exam, PDF page 6. This is a reasoned practice solution, not an official marking key.

[RFC 8235, §2.2](https://www.rfc-editor.org/rfc/rfc8235.html#section-2.2) gives the challenge parameter; [§6](https://www.rfc-editor.org/rfc/rfc8235.html#section-6) gives the reuse attack. The derivation here is mathematical reasoning, not a runtime experiment.
