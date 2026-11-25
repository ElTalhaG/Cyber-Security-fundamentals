# Exercise 1 - Understanding Digital Identity Credentials

## Credential attributes

The example credential contains `full_name`, `student_id`, `date_of_birth`, `email`, `university`, and `student_status`.

| Attribute | Classification | Why it matters |
|---|---|---|
| `full_name` | Direct identifier | Names the person and can be combined with other data to find her. |
| `student_id` | Direct identifier | Institution-specific identifier that can be looked up and linked to student records. |
| `date_of_birth` | Personal, identifying detail | Reveals age and can help distinguish or re-identify a person when combined with other data. |
| `email` | Direct identifier | Provides a contact route and commonly links accounts across services. |
| `university` | Student-verification context and personal affiliation | Identifies the institution and may reveal education or employment affiliation. |
| `student_status` | Student-verification attribute | Indicates whether the credential holder currently qualifies as a student. |

Categories can overlap. For example, the university and status are relevant to eligibility, but also reveal personal information. A student ID is especially useful for internal lookup, but the shop does not need it merely to check active student status.

## Minimum information needed

For the stated discount decision, the shop needs the active-status claim. It may also need confidence that the claim came from a trusted university and is current; those checks should come from authenticated credential evidence and validity/revocation checks, not from trusting an unverified JSON string.

## Privacy risks of sharing everything

Sharing the full credential gives the shop unnecessary identifiers, contact details, birth date, and affiliation. The shop could retain or link those values with purchase records, share them with other parties, or expose them in a breach. It also makes later re-identification easier. Data minimisation reduces the amount of information exposed if the shop misuses or loses the presentation.

## Source

[`identity_Mng_lab.pdf`](../../identity_Mng_lab.pdf), Exercise 1, pages 2-3.
