# Question 3 - Network Segmentation

## Task

An attacker compromises a workstation in a flat network and rapidly reaches file servers, development servers, and the domain controller. Identify the architectural weakness that most enabled lateral movement.

## Answer: C

The missing protection is **network segmentation with internal firewall enforcement**. A flat network provides broad communication paths between systems with different trust levels. After one workstation is compromised, the attacker can reach additional services and attempt to exploit them or use stolen credentials.

Segmentation places workstations, development systems, sensitive servers, and administration systems into separate security zones. Internal firewall rules allow only required communication between those zones, reducing the services reachable from a compromised workstation.

For example, an ordinary workstation might reach an approved file service while management interfaces and domain-controller administration are restricted to designated administration systems. The exact allowed flows depend on business needs. These are illustrative policy choices, not extra facts supplied by the scenario.

Creating VLANs alone is insufficient if unrestricted inter-VLAN routing remains enabled. The boundaries need enforced access rules, supported by identity controls and monitoring. Segmentation limits opportunities for lateral movement; it does not guarantee that permitted services or credentials are safe.

## Why the other answers do not fit

| Option | Explanation |
|---|---|
| A | Weak antivirus could help explain the first compromised workstation. The question specifically asks about rapid movement between internal systems. |
| B | Weak passwords could contribute to later compromise, but the stated architectural flaw is unrestricted connectivity in a flat network. |
| D | Inspection at the perimeter does not directly create boundaries between internal workstations, servers, and administration systems. |

## Remember

**A perimeter firewall does not replace internal segmentation.** Containing a compromised machine requires controls on communication inside the network as well.

## Source

Question sheet: PDF pages 1-2, third question; option D continues on page 2. Lecture 6: `sldies6.pdf`, PDF page 17 on prevention and page 23 on network security architecture.
