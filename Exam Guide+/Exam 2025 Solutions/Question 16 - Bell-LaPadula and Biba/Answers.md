# Question 16 - Bell-LaPadula and Biba

## Task

Choose the correct pair of information-flow rules for Bell-LaPadula and strict Biba.

## Answer: B

Bell-LaPadula protects confidentiality: **no read up, no write down**. A subject must not read data above its classification or move higher-classified information into a lower-classified object.

Strict Biba protects integrity in the opposite direction: **no read down, no write up**. A high-integrity subject should not consume lower-integrity information, and a lower-integrity subject must not write into a higher-integrity object.

Here up/down refer to the relevant security or integrity ordering, not a job title or whether a file is above another in a folder tree. These rules concern the standard simplified models asked about; some extensions support explicitly trusted mechanisms.

## Assessing the alternatives

- A swaps the usual confidentiality and integrity rule pairs.
- C gives Bell-LaPadula the wrong write rule and Biba the wrong write rule.
- D gives both models the wrong read rule.

## Remember

Bell-LaPadula prevents secret information flowing down. Strict Biba prevents low-integrity influence flowing up.

## Sources and scope

Original 2025 exam, PDF page 8. This is a reasoned practice solution, not an official marking key.
