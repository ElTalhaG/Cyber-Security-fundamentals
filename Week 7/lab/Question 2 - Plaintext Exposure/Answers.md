# Question 2 - Plaintext Exposure

## Task

A financial institution protects links between branches and headquarters. The scenario describes a router whose outbound links are encrypted while arriving traffic is processed in plaintext. Identify the most likely security risk.

## Intended answer: B

Plaintext at the router can be read or changed by someone who has access to the router's processing environment or to an actually unprotected incoming link. Encrypting the outgoing traffic does not undo an earlier disclosure and does not establish that the data was unchanged before outgoing encryption.

For example, a compromised router could read a transaction and alter its destination account before passing it to the next protected link. That next link would then carry the altered transaction. Link protection alone does not give the original application an end-to-end guarantee about its transaction.

## Important wording issue

The question blurs two different situations:

1. **Normal link encryption:** traffic arrives encrypted over a protected link, is decrypted by that link's receiving endpoint, and is then available for routing before the outgoing link protects it again. Plaintext inside the router is expected in the lecture's model. This fact alone does not prove a misconfiguration.
2. **An unprotected incoming link:** branch traffic crosses the incoming link in plaintext. This additionally exposes traffic on that link. If this is what the question means by the misconfiguration, B is the intended consequence.

A receiver normally decrypts incoming encrypted traffic; saying it fails to “encrypt inbound” traffic is technically imprecise. The worksheet does not establish from plaintext processing alone whether the incoming wire was protected. **Select B for the multiple-choice answer, while retaining this distinction.**

The lecture explicitly lists exposure at intermediate routers as a limitation of link encryption. An additional authenticated end-to-end channel could protect application content across a router, subject to trust in the application endpoints.

## Why the other answers do not fit

| Option | Explanation |
|---|---|
| A | Later encryption cannot reverse prior exposure. It also cannot detect an alteration that occurred before that protection began. |
| C | The stated scenario involves plaintext content, not just visible metadata. No additional end-to-end payload encryption is specified. |
| D | Nothing in the scenario establishes a routing loop. The issue described concerns confidentiality and potentially integrity. |

## Remember

**Encryption on a later hop does not protect data at an earlier exposure point. Plaintext at a link endpoint is not, by itself, evidence that the preceding link lacked encryption.**

## Source

Question sheet: PDF page 1, second question. Lecture 6: `sldies6.pdf`, PDF pages 10-11. The explanation above distinguishes the intended answer from the question's ambiguous description.
