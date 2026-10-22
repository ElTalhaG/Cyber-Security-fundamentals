# Question 14 - Kerberos KDC

## Task

Identify the two services that make up a Kerberos Key Distribution Center.

## Answer: D

The KDC provides the **Authentication Server (AS)** and the **Ticket-Granting Server (TGS)** roles.

The AS supports the initial authentication exchange and acquisition of a ticket-granting ticket. The client later uses that ticket with the TGS to request tickets for particular application services. The application service server is a separate role; it is not the second component of the KDC.

## Assessing the alternatives

- A and C incorrectly include the application Service Server in the KDC pair.
- B does not name the Kerberos AS/TGS components.

## Remember

KDC = AS + TGS. A service ticket goes to an application server; a TGT is used with the TGS.

## Sources and scope

Original 2025 exam, PDF page 7. This is a reasoned practice solution, not an official marking key.
