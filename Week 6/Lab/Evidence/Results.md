# Lab 6 - Recorded execution results

Execution date: 2026-10-07. The raw record is [namespace-demo.json](namespace-demo.json).

Environment: Docker Desktop Linux kernel `6.10.14-linuxkit`, `iptables v1.8.9 (nf_tables)`, amd64 container on Apple Silicon. The topology used isolated Linux namespaces with the handout's IP addresses and gateway interface names.

| Exercise | Scenarios passed | Observations |
|---|---|---|
| 1. Default policy | 7/7 | Internal TCP and replies worked; matching Windows IP/MAC reached TCP/23; Kali TCP/23, Windows TCP/22, wrong-MAC Windows TCP/23, outside ping, and ping to gateway were denied |
| 2. ICMP filtering | 3/3 | Slow ping received 3/3 replies; a 20-packet burst produced 4 ACCEPT / 16 DROP; another source could still ping |
| 3. Port-scanning defense | 6/6 | Ten new SYNs did not ban; the eleventh did; banned source could not ping; Windows remained allowed; shortened cooldown activated and later expired |

All 16 scenarios completed successfully. The renderer's cumulative exercise 1, 2, and 3 configurations were parsed with `iptables-restore --test` and applied to the disposable gateway during the run.

## Boundaries

- Windows was a Linux namespace with synthetic MAC `02:00:00:00:01:12`.
- TCP/23 was a banner service; this was not a Telnet authentication session on Metasploitable.
- The official Kali/Windows/Metasploitable VM set was not run.
- The 60-second attempt window and 300-second ban were configured and the ban activated. Expiration was demonstrated separately using a 2-second attempt window and 3-second ban, followed by four idle seconds. This does not claim a timed observation of the full 300-second cooldown.
- The recorded burst's ACCEPT count depends on bucket state from prior traffic; it is not an assertion that a fresh burst permits exactly four packets.
- No Mac or host firewall rules were changed. Only disposable namespaces were configured; their filter rules, virtual links, and processes were removed with the container.

Use the actual Windows NIC MAC, confirm masks/interfaces and routes, and repeat the checks on the course VMs to obtain course-environment evidence. See the [setup instructions](../README.md).
