# Question 23 - Vishing and OTP Harvesting

## Task

Use the support-call report and subsequent VPN login to identify the best-supported attack chain.

## Answer: C

The employee was called by someone impersonating IT support and was persuaded to read out a one-time password. That is **vishing → OTP harvesting → account takeover**.

A one-time code is still useful to an attacker if obtained and relayed while valid. The successful remote login is consistent with takeover. The suspicious-domain traffic supports investigation after the login, but does not establish the unrelated malware or DNS mechanisms in the other choices.

The account's country of origin and timestamps alone would not prove this chain; the decisive evidence is the employee's report of the impersonation call and disclosed OTP.

## Assessing the alternatives

- A: malware infection and DNS poisoning are not established by the stated evidence.
- B: MFA fatigue usually concerns repeated approval prompts or pressure to approve them; here the attacker explicitly collects the code.
- D: password spraying and an email/web phishing route are not the described chain.

## Remember

Name the observed mechanism. A phone impersonation that collects a code is OTP harvesting, not automatically MFA fatigue.

## Sources and scope

Original 2025 exam, PDF page 12. This is a reasoned practice solution, not an official marking key.
