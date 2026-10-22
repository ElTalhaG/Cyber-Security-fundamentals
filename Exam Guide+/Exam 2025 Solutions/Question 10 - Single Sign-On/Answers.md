# Question 10 - Single Sign-On

## Task

Identify the purpose of Single Sign-On among the supplied choices.

## Answer: A, best match

SSO lets a user authenticate once through an identity system and access participating services without maintaining or entering an independent password at each service. A relying service can accept an identity provider's assertion rather than already sharing a separate username/password secret with the user.

A is the best offered answer, but it should not be read as authentication to any arbitrary server without trust. The relying service must trust the identity provider or an appropriate federation arrangement, validate the assertion, and still make its own authorization decision.

## Assessing the alternatives

- B: preventing password reuse is not the defining purpose of SSO.
- C: SSO does not inherently replace a national identity system; such a system may itself support sign-on flows.
- D: SSO does not inherently make sessions unlinkable and may permit correlation through a shared identity.

## Remember

One user authentication can support multiple participating services; those services still require a trust arrangement.

## Sources and scope

Original 2025 exam, PDF page 5. This is a reasoned practice solution, not an official marking key.
