# Week 7 - Lab 7: Sample Questions on Lectures 6-7

All seven questions in the supplied worksheet are answered. This lab is a written multiple-choice exercise; the supplied Week 7 material does not request a program, VM configuration, or command-line experiment.

## Answers by question

Start with the [compact answer key](Answers.md), then open a question for its explanation:

1. [Link encryption](Question%201%20-%20Link%20Encryption/Answers.md) - **B**
2. [Plaintext exposure](Question%202%20-%20Plaintext%20Exposure/Answers.md) - **B**, intended answer with wording caveat
3. [Network segmentation](Question%203%20-%20Network%20Segmentation/Answers.md) - **C**
4. [Security architecture](Question%204%20-%20Security%20Architecture/Answers.md) - **B**
5. [System evolution](Question%205%20-%20System%20Evolution/Answers.md) - **C**
6. [Measurable objectives](Question%206%20-%20Measurable%20Objectives/Answers.md) - **B**
7. [Asset vulnerabilities](Question%207%20-%20Asset%20Vulnerabilities/Answers.md) - **A**

The worksheet does not number its questions. The folders number them in reading order; question 3 continues from PDF page 1 onto page 2. Each folder contains its own `Answers.md`. Illustrative examples are identified as examples and are not claimed to be measurements or additional assignment facts.

## Sources and coverage

| Source | Material used |
|---|---|
| [Question sheet](Exc_Sample%20Questions%20.pdf) | All three PDF pages and all seven questions |
| [Lecture 6](../../Week%206/Slides/sldies6.pdf) | PDF pages 10-11 for link/end-to-end encryption; pages 17 and 23 for prevention, segmentation, and architecture |
| [Lecture 7](../slides/02339-7.1.pdf) | PDF pages 2-4, 7, and 9 for planning, requirements, and evolution; pages 26-28 for the risk-analysis sequence |

Page references use physical PDF page numbers because the Lecture 7 deck's printed slide numbering diverges from its PDF pages. The source decks carry a `02270` footer, while this repository is the supplied `02339` course workspace; source files and their labels are preserved as provided.

The question sheet and supporting lecture material were read. All three worksheet pages were also visually inspected, including the question split across pages. The explanations are reasoned answers; no official answer key was supplied.

### Question 2 caveat

The lecture explicitly states that ordinary link encryption exposes data at intermediate routers. Plaintext processing there alone does not prove a misconfigured incoming link. The solution selects the worksheet's intended B answer while explaining the difference between normal router plaintext and an actually unencrypted link.

## Obsidian copies

Matching answer files are saved under:

```text
3. SEMESTER/CyberSec fundamentals 02339/3. Lab/Week 7/
  Question 1 - Link Encryption/Answers.md
  Question 2 - Plaintext Exposure/Answers.md
  Question 3 - Network Segmentation/Answers.md
  Question 4 - Security Architecture/Answers.md
  Question 5 - System Evolution/Answers.md
  Question 6 - Measurable Objectives/Answers.md
  Question 7 - Asset Vulnerabilities/Answers.md
```

The vault index is `3. Lab/Indexes/Week 7. Network Security and Administration.md`, linked from `1. Slides + Lab/W. 7.md`. The repository copies are tracked in Git; the vault is outside this repository.
