# Exam 2025 - Complete Worked Solutions

Worked answers to all 25 questions in the supplied Cybersecurity Fundamentals paper dated **19 December 2025**. The original header identifies course **02270**; the supplied repository is the 02339 workspace. The source is preserved unchanged.

Start with the [answer key](Answers.md), then open each question for its reasoning, assessment of alternatives, and revision takeaway. These are independent study solutions, not an official marking key. In particular, the privacy questions have assumptions or wording that do not support an unqualified single-letter answer.

## Questions by exam section

### Cryptography

- [Question 1: Hash Functions](Question%2001%20-%20Hash%20Functions/Answers.md)
- [Question 2: Double Encryption](Question%2002%20-%20Double%20Encryption/Answers.md)
- [Question 3: Replay and Freshness](Question%2003%20-%20Replay%20and%20Freshness/Answers.md)
- [Question 4: Digital Signatures](Question%2004%20-%20Digital%20Signatures/Answers.md)
- [Question 5: Non-Repudiation](Question%2005%20-%20Non-Repudiation/Answers.md)
- [Question 6: Arbitrated Protocols](Question%2006%20-%20Arbitrated%20Protocols/Answers.md)

### Protocol Security

- [Question 7: TLS Authentication](Question%2007%20-%20TLS%20Authentication/Answers.md)
- [Question 8: Protocol Replay Trace](Question%2008%20-%20Protocol%20Replay%20Trace/Answers.md)
- [Question 9: Repairing the Replay](Question%2009%20-%20Repairing%20the%20Replay/Answers.md)
- [Question 10: Single Sign-On](Question%2010%20-%20Single%20Sign-On/Answers.md)

### Privacy

- [Question 11: Onion Routing](Question%2011%20-%20Onion%20Routing/Answers.md)
- [Question 12: Schnorr Proofs](Question%2012%20-%20Schnorr%20Proofs/Answers.md)

### Authentication

- [Question 13: Identity Models](Question%2013%20-%20Identity%20Models/Answers.md)
- [Question 14: Kerberos KDC](Question%2014%20-%20Kerberos%20KDC/Answers.md)

### Access Control

- [Question 15: Roles and Attributes](Question%2015%20-%20Roles%20and%20Attributes/Answers.md)
- [Question 16: Bell-LaPadula and Biba](Question%2016%20-%20Bell-LaPadula%20and%20Biba/Answers.md)

### Network Security

- [Question 17: End-to-End Encryption](Question%2017%20-%20End-to-End%20Encryption/Answers.md)
- [Question 18: Network Segmentation](Question%2018%20-%20Network%20Segmentation/Answers.md)

### Governance

- [Question 19: Risk Evaluation](Question%2019%20-%20Risk%20Evaluation/Answers.md)
- [Question 20: Operational Security Planning](Question%2020%20-%20Operational%20Security%20Planning/Answers.md)

### Software Security

- [Question 21: Logic Bombs](Question%2021%20-%20Logic%20Bombs/Answers.md)
- [Question 22: Buffer Overflows](Question%2022%20-%20Buffer%20Overflows/Answers.md)

### Social Engineering

- [Question 23: Vishing and OTP Harvesting](Question%2023%20-%20Vishing%20and%20OTP%20Harvesting/Answers.md)
- [Question 24: Vishing](Question%2024%20-%20Vishing/Answers.md)

### Legal Aspects

- [Question 25: Privacy Governance](Question%2025%20-%20Privacy%20Governance/Answers.md)

## Sources and review boundaries

- [Original exam](../Exam%20Paper%202025.pdf): all 13 PDF pages, including instructions and all 25 questions, read and visually inspected.
- [Supplied exam guidelines](../exam%20guidelines.pdf): retained as a separate course source. Historical dates in these files are not a live timetable for the current examination.
- Existing lecture notes/material support the earlier course topics. Additional primary sources were consulted for technical distinctions:
  - [RSA-OAEP size limits, RFC 8017 §7.1.1](https://www.rfc-editor.org/rfc/rfc8017.html#section-7.1.1).
  - [TLS modes and authentication, RFC 8446](https://www.rfc-editor.org/rfc/rfc8446.html).
  - [DTU OFMC tutorial](https://www2.compute.dtu.dk/~samo/OFMC-tutorial.pdf), especially PDF pages 10-12 on strong authentication and replay.
  - [Original Tor design](https://svn-archive.torproject.org/svn/projects/design-paper/tor-design.html) on relay visibility. The exam's no-traffic-analysis abstraction must be kept separate from real Tor limitations.
  - [Schnorr identification and commitment reuse, RFC 8235](https://www.rfc-editor.org/rfc/rfc8235.html), §§2.2 and 6.
  - [NIST ABAC definition](https://www.nist.gov/publications/guide-attribute-based-access-control-abac-definition-and-considerations-1).
  - [NIST Privacy Framework guidance](https://www.nist.gov/privacy-framework/getting-started-0) for governance/accountability terminology; not a source of GDPR legal duties.

The OFMC trace in Q8 is analyzed manually. OFMC was not run, and the proposed repair directions in Q9 are not presented as formally verified protocols. The mathematical derivation in Q12 is worked reasoning, not a software experiment. This paper requires written multiple-choice answers; no additional program or VM setup is needed.

## Obsidian copy

Matching question folders are saved in:

```text
3. SEMESTER/CyberSec fundamentals 02339/3. Lab/Exam 2025/
```

The vault index is `3. Lab/Indexes/Exam 2025. Worked Answers.md`. The complete answer key is also copied to `3. Lab/Exam 2025/Answers.md`. The vault is outside this Git repository, while these repository copies are versioned for use in VS Code and GitHub.
