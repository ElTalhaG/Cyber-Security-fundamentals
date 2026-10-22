# Question 1 - Link Encryption

## Task

Identify the statement that best describes link encryption in communication security.

## Answer: B

Link encryption protects each network link separately. In the lecture's model, encryption operates at the Data Link Layer: a sending link endpoint encrypts the traffic, and the receiving link endpoint decrypts it. An intermediate router can process the plaintext before the next outgoing link encrypts it again.

```text
Branch -- encrypted link --> Router -- encrypted link --> Headquarters
                              |
                     plaintext during processing
```

The links can be protected while an intermediate node still sees the data. That node must therefore be trusted and secured. If a separate end-to-end encryption layer is also used, its protected content can remain encrypted while link encryption is removed; that additional layer is not assumed in this question.

## Why the other answers do not fit

| Option | Explanation |
|---|---|
| A | Keeping content encrypted across intermediate nodes describes end-to-end protection, not the hop-by-hop mechanism asked about. |
| C | Link encryption is not defined as payload-only protection. It may also conceal network-layer headers while traffic crosses a link; the framing needed to operate that particular link is a separate matter. |
| D | This describes protection implemented by end hosts. Link encryption involves the endpoints of each protected link and may require network-device support. |

## Remember

**Link encryption protects individual hops; end-to-end encryption protects content between the communicating endpoints.** Neither guarantees that a compromised endpoint cannot read plaintext available there.

## Source

Question sheet: PDF page 1, first question. Lecture 6: `sldies6.pdf`, PDF pages 10-11, communication-security slides. Page numbers refer to PDF pages.
