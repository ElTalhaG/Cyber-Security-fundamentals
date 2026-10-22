# Question 18 - Network Segmentation

## Task

Choose the best description of network segmentation.

## Answer: B

Segmentation divides systems into zones and enforces permitted communication between those zones. It limits the reachable services and lateral-movement paths available after an endpoint is compromised.

For example, ordinary workstations, development servers, and administration systems can have different access rules. Separate VLANs without meaningful controls on inter-VLAN traffic do not by themselves provide the intended containment. Segmentation supports least privilege but does not guarantee that every allowed service is safe.

## Assessing the alternatives

- A: segmentation does not itself encrypt all traffic.
- C: a perimeter attack-blocking tool is not the definition of internal zoning and access control.
- D: segmentation may distribute trust boundaries; centralizing every flow is not its defining objective.

## Remember

Create and enforce internal trust boundaries to reduce the blast radius of a compromise.

## Sources and scope

Original 2025 exam, PDF page 9. This is a reasoned practice solution, not an official marking key.
