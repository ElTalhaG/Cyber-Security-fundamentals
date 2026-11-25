# Exercise 3 - Age Verification Without Date of Birth

## Minimal presentation

For the sample birth date, Alice is over 18. The simplified presentation can be:

```json
{
  "age_over_18": true
}
```

It withholds the exact date of birth, full name, and email address. The service learns only whether Alice satisfies the age threshold, rather than her birthday and contact identity.

## How a verifier can trust the result

The Boolean alone is only an assertion. In a real system, a trusted issuer must authenticate the underlying birth-date credential, and the holder must provide a cryptographic proof that the signed date meets the threshold without exposing the date itself. An anonymous credential or suitable zero-knowledge predicate proof can provide that property. The verifier checks the issuer's proof and credential validity, expiry, and revocation without receiving the full date of birth.

If a service simply accepts the JSON value `true`, a user can edit it. The worksheet's JSON is therefore a simulation of the information disclosed, not a complete credential protocol.

## Privacy comparison

The full credential exposes directly identifying and linkable details. The age-only result minimizes disclosure and can reduce profiling and breach impact. It still reveals that Alice meets an age threshold, which is itself information; the service should retain it only as needed.

## Source

[`identity_Mng_lab.pdf`](../../identity_Mng_lab.pdf), Exercise 3, pages 3-4.
