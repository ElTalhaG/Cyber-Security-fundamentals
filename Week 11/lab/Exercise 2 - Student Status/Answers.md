# Exercise 2 - Student Discount Through Selective Disclosure

## Minimal presentation

The shop asks only whether Alice is currently an active student, so the simplified presentation is:

```json
{
  "student_status": "active"
}
```

## Comparison

The original credential contains six attributes. The presentation contains one: `student_status`. It withholds `full_name`, `student_id`, `date_of_birth`, `email`, and `university`.

This reduces the information available to the shop and limits linkage between Alice's student credential and her shopping account. The tradeoff is that the shop sees less context; the credential system must still give it trustworthy evidence that the status claim is from an authorized issuer and remains valid.

The JSON above is a teaching example, not a cryptographic proof. A real verifier must validate the issuer and credential, check expiry and revocation, and use an authenticated selective-disclosure mechanism.

## Source

[`identity_Mng_lab.pdf`](../../identity_Mng_lab.pdf), Exercise 2, pages 2-3.
