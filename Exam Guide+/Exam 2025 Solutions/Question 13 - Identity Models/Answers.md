# Question 13 - Identity Models

## Task

Identify the combination of models used for internal SSO and access to partner systems through SAML or OpenID Connect.

## Answer: A

The scenario explicitly combines **SSO internally** with **federated identity externally**. Internal users reuse a central authentication experience across participating applications. Federation allows a partner organization to rely on identity assertions from the employee's corporate identity provider.

These ideas overlap: federation can support an SSO experience across organizational boundaries. They are not mutually exclusive technologies. A is the option that matches how this question assigns their roles.

## Assessing the alternatives

- B: the internal applications are described as having SSO, not independent identity silos.
- C: this reverses the scenario's intended internal/external labels.
- D: it omits the explicitly described internal SSO arrangement.

## Remember

SSO describes the user sign-on experience across services; federation concerns trust and identity assertions across domains.

## Sources and scope

Original 2025 exam, PDF page 7. This is a reasoned practice solution, not an official marking key.
