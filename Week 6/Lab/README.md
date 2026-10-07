# Week 6 - Lab 6: Firewalls

Completed written solutions and executable IPv4 firewall rules for all three exercises, including the optional port-scanning defense.

## Start here

| Exercise | Answers | Implementation |
|---|---|---|
| 1. Default policy | [Answers](Exercise%201%20-%20Default%20Policy/Answers.md) | Baseline rules, stateful replies, Windows IP and MAC restriction for Telnet |
| 2. ICMP filtering | [Answers](Exercise%202%20-%20ICMP%20Filtering/Answers.md) | Per-source ping limiter, 1 packet/second with burst 5 |
| 3. Port-scanning defense (optional) | [Answers](Exercise%203%20-%20Port%20Scanning%20Defense/Answers.md) | `recent` tracking, 11th new TCP SYN in 60 seconds triggers a sliding 300-second ban |

Each exercise has `rules.v4.template` containing its contribution and `rules.sample.v4` containing a complete cumulative ruleset. **The samples use the demo MAC, not the actual Windows VM MAC.** Use the renderer below for the course VMs.

## Topology from Figure 1

| Machine/interface | Address | Location |
|---|---|---|
| Kali | `192.168.1.11` | LAN 2, outside |
| Windows XP | `192.168.1.12` | LAN 2, outside |
| Gateway `eth0` | `192.168.1.1` | LAN 2, outside |
| Gateway `eth1` | `192.168.2.1` | LAN 1, inside |
| Metasploitable | `192.168.2.10` | LAN 1, inside |

The handout shows host addresses but does not state prefix lengths. These solutions assume both LANs are `/24`. Inspect the VM configuration to confirm this and the interface names. No NAT is needed between these two routed LANs.

## Generate and apply on the course gateway

Requirements: Linux gateway, Python 3, `iptables`, `iptables-restore`, `conntrack`, `mac`, `hashlimit`, and `recent` match support. The scripts target IPv4; they do not configure IPv6. Run these steps from this `Lab` directory on the gateway's VM console. The rules replace the **filter table** and block new connections to the gateway itself, including SSH; use the console rather than an SSH session.

1. On Windows, run `ipconfig /all` and find the Physical Address for its LAN 2 NIC. Convert hyphens to colons. It must be the NIC that sends traffic toward the gateway.
2. On the gateway, inspect interfaces and routes:

```sh
ip -br addr
ip route
ip neigh show dev eth0
```

The neighbor entry for `192.168.1.12` can corroborate Windows' MAC after Windows sends traffic. MACs are not supplied by the handout, and the generator requires one explicitly.

3. Save the current firewall and forwarding setting locally:

```sh
mkdir -p .local
sudo iptables-save > .local/firewall-before.v4
sysctl -n net.ipv4.ip_forward > .local/ip-forward-before.txt
```

4. Enter the real MAC and generate the selected cumulative exercise ruleset:

```sh
read -r -p 'Windows LAN 2 MAC (colon-separated): ' WINDOWS_LAB_MAC
python3 Shared/render_rules.py --exercise 1 \
  --windows-mac "$WINDOWS_LAB_MAC" > .local/exercise1.v4
cat .local/exercise1.v4
sudo iptables-restore --test < .local/exercise1.v4
sudo iptables-restore < .local/exercise1.v4
sudo sysctl -w net.ipv4.ip_forward=1
sudo iptables -nvL --line-numbers
```

Use `--exercise 2` or `--exercise 3` with a matching output filename to add the later protections. Every generated ruleset is complete and includes the earlier exercises. Override interface names with `--outside-interface enp0s3 --inside-interface enp0s8` when the VM uses those names. Generated `.local` files and backups are ignored by Git.

5. Ensure return routing exists. For example, on Kali:

```sh
sudo ip route replace 192.168.2.0/24 via 192.168.1.1
```

On Metasploitable:

```sh
sudo ip route replace 192.168.1.0/24 via 192.168.2.1
```

Windows must use `192.168.1.1` for the internal subnet. If its existing default route already does this, no additional route is needed. Otherwise, in its administrator command prompt:

```text
route add 192.168.2.0 mask 255.255.255.0 192.168.1.1
```

6. Restore the saved configuration when finished:

```sh
sudo iptables-restore < .local/firewall-before.v4
sudo sysctl -w net.ipv4.ip_forward="$(cat .local/ip-forward-before.txt)"
```

The forwarding change is temporary; persistence across boot is not required by the handout. Restoring firewall rules does not erase existing connection-tracking entries. Use fresh connections when comparing policies.

## Isolated runnable demonstration

The demonstration builds the same two-LAN topology with four disposable Linux namespaces. It uses a Linux Windows stand-in and a TCP/23 banner listener instead of a full Windows/Metasploitable Telnet session. It never uses the Mac's firewall or Docker's host network.

From this directory:

```sh
docker build --platform linux/amd64 -t cybersec-week6-lab:local Shared
docker run --rm --platform linux/amd64 --network none \
  --cap-add NET_ADMIN --cap-add SYS_ADMIN \
  --security-opt seccomp=unconfined --security-opt apparmor=unconfined \
  -v "$PWD:/lab:ro" cybersec-week6-lab:local \
  python3 /lab/Shared/namespace_demo.py
```

The capabilities allow namespace creation, virtual links, firewall configuration, and a private `/proc` mount for the gateway's forwarding setting. The repository mount is read-only. The runner removes its namespaces and the container is removed on exit. Docker Desktop must be running. The image is local and is not pushed to a registry.

See [recorded results](Evidence/Results.md) and [raw observations](Evidence/namespace-demo.json). The demonstration completed 16 scenarios successfully on 2026-10-07. The full 300-second idle ban was configured and activated; expiration was demonstrated separately with shortened 2/3-second intervals. The actual course VMs have not been exercised.

## Written copies in Obsidian

Matching `Answers.md` files are also saved under the course vault's `3. Lab/Week 6/` exercise folders, with an index in `3. Lab/Indexes/`. Repository copies make the answers available in VS Code and GitHub.

## Sources

- Assignment: [Exercise-network security.pdf](Exercise-network%20security.pdf), pages 1-3, Figure 1.
- [Netfilter packet-filtering HOWTO](https://www.iptables.org/documentation/HOWTO/packet-filtering-HOWTO-7.html) for chains, rules, and traversal. This is the older reference named in the handout.
- [Debian's iptables extension manual](https://manpages.debian.org/trixie/iptables/iptables-extensions.8.en.html) for current `mac`, `hashlimit`, and `recent` options.
