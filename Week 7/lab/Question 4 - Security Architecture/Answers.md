# Question 4 - Security Architecture

## Task

Choose the best description of the role of a network security architecture.

## Answer: B

A network security architecture is the coordinated design of controls, network boundaries, and policies used to protect network assets. It connects security goals to the placement and operation of concrete mechanisms.

For example, the lecture's architecture separates a DMZ, administration network, and development network and includes firewalls, endpoint protection, intrusion detection, and a SIEM. The value comes from how those controls work together: permitted communication is defined, sensitive systems have boundaries, suspicious behavior can be detected, and responsible staff can respond.

An address plan and topology diagram can document parts of that design, but the architecture also needs trust assumptions, allowed flows, and control responsibilities. It should be reviewed when services, users, or threats change.

## Why the other answers do not fit

| Option | Explanation |
|---|---|
| A | Topology and addressing alone do not specify how assets are protected. Describing the architecture as static also omits adaptation as the system changes. |
| C | Security architecture includes internal boundaries and operational controls. It is not limited to hardware at the network perimeter. |
| D | Automation can support monitoring and enforcement, but it does not replace human decisions about acceptable risk, business needs, and accountability. |

## Remember

**Security architecture connects policy, boundaries, prevention, detection, and response into a coherent design.**

## Source

Question sheet: PDF page 2, fourth question. Lecture 6: `sldies6.pdf`, PDF pages 17 and 23. Lecture 7: `02339-7.1.pdf`, PDF pages 2-4 for planning and the relationship between policy and controls.
