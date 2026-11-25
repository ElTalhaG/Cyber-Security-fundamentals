# Question 13 - Validate input on the server; client-side checks are insufficient

## Scenario / question

A browser-side username filter is bypassed, and the backend receives binary data that causes a malfunction.

## Answer

**B** - Validate input on the server; client-side checks are insufficient.

## Reasoning

Client-side validation can be edited or bypassed, so the server must independently validate the actual bytes it receives against the allowed format and reject anything else. Validation should happen at the trust boundary. Encoding is not a replacement for validating expected input.

## Takeaway

Validate input on the server; client-side checks are insufficient is identified by the mechanism and evidence in the scenario. Read the details carefully: channel, target, impersonated identity, and the actual effect often distinguish similar terms.

## Sources

Worksheet p. 5; lecture slide PDF pp. 84-86. See [`Exc_Sample Questions  - 11-12.pdf`](../../Exc_Sample%20Questions%20%20-%2011-12.pdf) and [`Software Security and Social Engineering slides.pdf`](../../Software%20Security%20and%20Social%20Engineering%20slides.pdf).
