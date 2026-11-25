# Question 10 - Lack of database isolation / excessive privileges

## Scenario / question

A low-privilege analytics account can read authentication tables because unrelated data domains share an over-permissive schema.

## Answer

**C** - Lack of database isolation / excessive privileges.

## Reasoning

The access path exists because unrelated data is not adequately isolated and the analytics account has broader table permissions than its task requires. Enforce least privilege and separate security boundaries. This is not primarily a weak-password-hashing issue or a login-form bypass.

## Takeaway

Lack of database isolation / excessive privileges is identified by the mechanism and evidence in the scenario. Read the details carefully: channel, target, impersonated identity, and the actual effect often distinguish similar terms.

## Sources

Worksheet p. 4; lecture slide PDF pp. 18 and 84. See [`Exc_Sample Questions  - 11-12.pdf`](../../Exc_Sample%20Questions%20%20-%2011-12.pdf) and [`Software Security and Social Engineering slides.pdf`](../../Software%20Security%20and%20Social%20Engineering%20slides.pdf).
