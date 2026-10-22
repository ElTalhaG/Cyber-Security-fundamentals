# Question 7 - Asset Vulnerabilities

## Task

A company identifies critical assets and immediately selects controls without examining how those assets could be compromised. Identify the skipped risk-analysis step.

## Answer: A

The company skipped **identifying possible vulnerabilities associated with each asset**. An asset inventory describes what needs protection. Vulnerability analysis examines weaknesses and failure paths that could compromise its confidentiality, integrity, or availability.

For example, identifying a customer database as critical does not tell the company which controls fit. It should consider scenarios such as excessive user permissions, stolen credentials, accidental deletion, untested backups, or a storage failure. Different scenarios suggest different controls and priorities.

The lecture presents this sequence:

1. Identify assets.
2. Determine possible vulnerabilities.
3. Estimate the likelihood of exploitation.
4. Estimate expected annual loss.
5. Examine applicable controls and their costs.
6. Estimate the annual savings from those controls.

The immediate skipped step is number 2. The scenario also provides no evidence that likelihood and loss were assessed, but those are not the best answer among the listed alternatives.

## Keep the terms separate

| Term | Example |
|---|---|
| Asset | Customer database |
| Threat or event | An attacker tries to obtain customer records |
| Vulnerability | An account has excessive database permissions |
| Impact | Customer information is disclosed |
| Control | Restrict privileges and review access |

This example illustrates the reasoning; it is not a claim about the company in the question. Consider accidental errors and physical failures as well as malicious attacks. The lecture examines vulnerabilities against confidentiality, integrity, and availability.

## Why the other answers do not fit

| Option | Explanation |
|---|---|
| B | Control costs matter later when comparing options. They do not identify how an asset might be compromised. |
| C | Enforcement guidelines concern applying policy. The immediate analytical gap is the missing examination of asset vulnerabilities. |
| D | The question explicitly says the assets have already been identified. |

## Remember

**Identify the asset, examine how it can fail or be attacked, assess the risk, then select suitable controls.**

## Source

Question sheet: PDF page 3, seventh question. Lecture 7: `02339-7.1.pdf`, PDF page 26 for the risk-analysis sequence, page 27 for assets, and page 28 for vulnerability analysis across confidentiality, integrity, and availability.
