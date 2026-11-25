# Question 9 - SQL injection through the username input

## Scenario / question

The backend concatenates username and password input into SQL. The developer hashes the password and assumes that prevents injection.

## Answer

**B** - SQL injection through the username input.

## Reasoning

The username is still inserted directly into the SQL text, so an attacker can change the query through that input. Hashing the password does not make the username safe and is not a substitute for query parameterization. Use prepared statements so input values remain data, and apply least-privilege database permissions.

## Takeaway

SQL injection through the username input is identified by the mechanism and evidence in the scenario. Read the details carefully: channel, target, impersonated identity, and the actual effect often distinguish similar terms.

## Sources

Worksheet p. 3; lecture slide PDF pp. 81-84. See [`Exc_Sample Questions  - 11-12.pdf`](../../Exc_Sample%20Questions%20%20-%2011-12.pdf) and [`Software Security and Social Engineering slides.pdf`](../../Software%20Security%20and%20Social%20Engineering%20slides.pdf).
