# Question 2 - Physical access, rogue device, local DNS spoofing, credential harvesting

## Scenario / question

An unmanaged device is connected to a meeting-room switch. Forged DNS responses on the local VLAN redirect SSO users to a fake sign-in page.

## Answer

**C** - Physical access, rogue device, local DNS spoofing, credential harvesting.

## Reasoning

The sequence in C matches the evidence: someone gets a rogue device onto the local network, spoofs DNS responses on that VLAN, redirects users to a lookalike SSO portal, and collects credentials. No email or SMS campaign is needed. The scenario does not establish that the core DNS server was compromised.

## Takeaway

Physical access, rogue device, local DNS spoofing, credential harvesting is identified by the mechanism and evidence in the scenario. Read the details carefully: channel, target, impersonated identity, and the actual effect often distinguish similar terms.

## Sources

Worksheet p. 1; lecture slide PDF pp. 23-24 on pharming and DNS poisoning. See [`Exc_Sample Questions  - 11-12.pdf`](../../Exc_Sample%20Questions%20%20-%2011-12.pdf) and [`Software Security and Social Engineering slides.pdf`](../../Software%20Security%20and%20Social%20Engineering%20slides.pdf).
