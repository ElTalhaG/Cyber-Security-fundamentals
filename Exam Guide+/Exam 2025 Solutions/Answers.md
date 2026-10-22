# Exam 2025 - Worked Answer Key

All 25 questions have a full solution in a separate question folder. This is a reasoned practice key, **not an official marking key**. An asterisk marks a scope assumption or wording caveat that matters to understanding the choice.

| Question | Topic | Working choice | Exam PDF page |
|---|---|---|---|
| [1](Question%2001%20-%20Hash%20Functions/Answers.md) | Hash Functions | **C** | 2 |
| [2](Question%2002%20-%20Double%20Encryption/Answers.md) | Double Encryption | **B*** | 2 |
| [3](Question%2003%20-%20Replay%20and%20Freshness/Answers.md) | Replay and Freshness | **C** | 2 |
| [4](Question%2004%20-%20Digital%20Signatures/Answers.md) | Digital Signatures | **C*** | 3 |
| [5](Question%2005%20-%20Non-Repudiation/Answers.md) | Non-Repudiation | **B** | 3 |
| [6](Question%2006%20-%20Arbitrated%20Protocols/Answers.md) | Arbitrated Protocols | **B** | 3 |
| [7](Question%2007%20-%20TLS%20Authentication/Answers.md) | TLS Authentication | **D*** | 4 |
| [8](Question%2008%20-%20Protocol%20Replay%20Trace/Answers.md) | Protocol Replay Trace | **B** | 4-5 |
| [9](Question%2009%20-%20Repairing%20the%20Replay/Answers.md) | Repairing the Replay | **D** | 5 |
| [10](Question%2010%20-%20Single%20Sign-On/Answers.md) | Single Sign-On | **A*** | 5 |
| [11](Question%2011%20-%20Onion%20Routing/Answers.md) | Onion Routing | **B/C - ambiguous** | 6 |
| [12](Question%2012%20-%20Schnorr%20Proofs/Answers.md) | Schnorr Proofs | **A*** | 6 |
| [13](Question%2013%20-%20Identity%20Models/Answers.md) | Identity Models | **A** | 7 |
| [14](Question%2014%20-%20Kerberos%20KDC/Answers.md) | Kerberos KDC | **D** | 7 |
| [15](Question%2015%20-%20Roles%20and%20Attributes/Answers.md) | Roles and Attributes | **D*** | 8 |
| [16](Question%2016%20-%20Bell-LaPadula%20and%20Biba/Answers.md) | Bell-LaPadula and Biba | **B** | 8 |
| [17](Question%2017%20-%20End-to-End%20Encryption/Answers.md) | End-to-End Encryption | **B** | 9 |
| [18](Question%2018%20-%20Network%20Segmentation/Answers.md) | Network Segmentation | **B** | 9 |
| [19](Question%2019%20-%20Risk%20Evaluation/Answers.md) | Risk Evaluation | **B** | 10 |
| [20](Question%2020%20-%20Operational%20Security%20Planning/Answers.md) | Operational Security Planning | **B** | 11 |
| [21](Question%2021%20-%20Logic%20Bombs/Answers.md) | Logic Bombs | **B** | 11 |
| [22](Question%2022%20-%20Buffer%20Overflows/Answers.md) | Buffer Overflows | **C** | 11 |
| [23](Question%2023%20-%20Vishing%20and%20OTP%20Harvesting/Answers.md) | Vishing and OTP Harvesting | **C** | 12 |
| [24](Question%2024%20-%20Vishing/Answers.md) | Vishing | **B** | 12 |
| [25](Question%2025%20-%20Privacy%20Governance/Answers.md) | Privacy Governance | **B** | 13 |

## Compact revision key

```text
01-05: C  B* C  C* B
06-10: B  D* B  D  A*
11-15: B/C  A* A  D  D*
16-20: B  B  B  B  B
21-25: B  C  C  B  B
```

## Ambiguities and qualifications

- **Q2 - B, intended:** direct RSA over bulk AES ciphertext is inefficient and does not automatically double security. The claim that an extra layer adds no meaningful security is not a universal theorem.
- **Q4 - C:** identity attribution requires a trusted identity-to-key binding, not just a signature under any presented key.
- **Q7 - D:** the answer assumes the usual server-authenticated DH handshake. TLS also supports modes without a DH share.
- **Q10 - A:** participating servers need a trust relationship with an identity provider; SSO does not authorize a client at arbitrary unrelated services.
- **Q11 - B/C:** the entry relay sees its immediate client, while an honest mixing hop can protect end-to-end association under the question's ideal assumptions. The wording does not make a unique grading choice reliable. Read the full analysis instead of treating this as an official multi-answer key.
- **Q12 - A:** reusing a Schnorr commitment across different challenges exposes the witness. C is also valid for the common independent binary-challenge version. The question does not specify the challenge space or include its referenced lecture variant.
- **Q15 - D, best descriptive match:** role-based grants plus context fit the combined description. ABAC alone can also express roles as attributes, so C is technically capable.

The paper requests exactly one marked choice per question. This study key preserves technical ambiguity rather than claiming that marking multiple boxes would be valid. A lecturer's official key or clarification is needed to resolve the intended grading of the privacy questions.

## Review priorities

1. For Q1 and Q22, notice that the prompt asks for the **incorrect** statement.
2. Work through Q8-Q9 as a pair: identify which participant accepts a replay before deciding whether a change repairs it.
3. Separate local visibility at an onion relay from end-to-end message linking in Q11.
4. Derive the two-transcript Schnorr witness extraction in Q12 and state the challenge size before quoting a probability.
5. Learn the opposite confidentiality/integrity flow directions in Q16.

See [the solution guide](README.md) for sources, coverage, and the Obsidian copy.
