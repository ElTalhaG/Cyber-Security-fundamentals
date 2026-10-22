# Lab 7 - Answer Key

The worksheet contains seven unnumbered multiple-choice questions based on Lectures 6 and 7. Numbers below follow their order in the PDF.

| Question | Topic | Answer | Reason |
|---|---|---|---|
| [1](Question%201%20-%20Link%20Encryption/Answers.md) | Link encryption | **B** | Each link is protected independently; intermediate link endpoints decrypt and re-encrypt traffic. |
| [2](Question%202%20-%20Plaintext%20Exposure/Answers.md) | Plaintext exposure | **B, intended** | Later outgoing encryption cannot undo earlier exposure. The question's description of a misconfiguration is ambiguous; see below. |
| [3](Question%203%20-%20Network%20Segmentation/Answers.md) | Lateral movement | **C** | Missing internal segmentation and firewall boundaries permit broad internal reachability. |
| [4](Question%204%20-%20Security%20Architecture/Answers.md) | Security architecture | **B** | Architecture coordinates controls, segmentation, and policies to protect network assets. |
| [5](Question%205%20-%20System%20Evolution/Answers.md) | System evolution | **C** | Security planning needs procedures to update controls as the environment changes. |
| [6](Question%206%20-%20Measurable%20Objectives/Answers.md) | Evaluating an IDS | **B** | Measurable objectives, benchmarks, and a baseline are needed to assess effectiveness. |
| [7](Question%207%20-%20Asset%20Vulnerabilities/Answers.md) | Risk-analysis sequence | **A** | After identifying assets, examine their vulnerabilities before choosing controls. |

**Compact key: B - B - C - B - C - B - A.** These are reasoned solutions, not a supplied official marking key.

## Question 2 clarification

In normal link encryption, incoming traffic is decrypted and may be processed as plaintext inside an intermediate router. That is a known limitation of link encryption, not proof by itself that an incoming link is unprotected. An actually unencrypted incoming link would additionally expose traffic on that link. B is the intended multiple-choice answer, but the question blurs these two situations. The full answer preserves this distinction.

## Full solutions and sources

Each linked answer contains a task summary, reasoning, discussion of alternatives, a revision takeaway, and relevant PDF page references. See [the lab index](README.md) for source locations and Obsidian navigation.
