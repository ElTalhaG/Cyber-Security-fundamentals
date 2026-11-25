# Exercise 4 - Designing a Basic Verifier

The exercise asks us to evaluate the presented attributes. It says authenticity, validity, expiry, and revocation are checked separately.

| Presentation | Decision | Reason |
|---|---|---|
| `{"student_status":"active"}` | **Accept**, assuming the separate credential checks pass | The required attribute is present and has the required value. |
| `{"student_status":"inactive"}` | **Reject** | The status is present but does not satisfy the active-student requirement. |
| `{"university":"Example University"}` | **Reject** | The required `student_status` attribute is missing. A university name alone does not establish current student status. |

Acceptance is conditional on the separate authenticity and validity checks passing. The attribute-only rule does not let the verifier infer that a JSON presentation is genuine.

## Source

[`identity_Mng_lab.pdf`](../../identity_Mng_lab.pdf), Exercise 4, pages 4-5.
