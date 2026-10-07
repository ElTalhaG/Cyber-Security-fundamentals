# Exercise 2 - ICMP Filtering

## Task

Allow legitimate ICMP echo requests from outside LAN 2 to inside LAN 1 and limit their rate from each source.

## Solution

Permit an average of **one echo request per second per source IP**, with a maximum initial burst of five. The handout does not prescribe a rate, so these are explicit design choices. A token bucket allows a small burst for diagnostics while dropping sustained excess traffic.

Use a separate chain to make the allow/drop decision terminal:

```sh
iptables -N W6_PING
iptables -A W6_PING -m hashlimit \
  --hashlimit-upto 1/second --hashlimit-burst 5 \
  --hashlimit-mode srcip --hashlimit-name w6_ping \
  --hashlimit-htable-expire 60000 -j ACCEPT
iptables -A W6_PING -j DROP

# Position this jump BEFORE the generic ESTABLISHED,RELATED acceptance rule.
iptables -I FORWARD 1 -i eth0 -o eth1 \
  -s 192.168.1.0/24 -d 192.168.2.0/24 \
  -p icmp --icmp-type echo-request -j W6_PING
```

These commands illustrate the mechanism on the exercise 1 baseline. The provided renderer places the rules in a full ordered configuration after invalid-packet and source-subnet checks. It avoids repeatedly inserting jumps when reapplying the exercise.

The condition covers all inside hosts, not only Metasploitable. The grouping key is the source IP, so requests from one outside host share a bucket even when that host pings several internal destinations. Different source IPs have different buckets. [Hashlimit and limit documentation](https://manpages.debian.org/trixie/iptables/iptables-extensions.8.en.html#hashlimit).

## Why `hashlimit` rather than plain `limit`

A single rule with `-m limit --limit 1/second` shares one token bucket across all traffic matching that rule. A noisy host could consume the allowance for everyone. `hashlimit --hashlimit-mode srcip` creates independent source-address buckets, which directly satisfies the per-source requirement.

This is an average rate with a burst allowance, not a strict maximum of one packet in every calendar second. Five requests can pass immediately from a fresh bucket; tokens then replenish at one per second. The entry expires after 60 seconds of inactivity, after which a fresh bucket can have a fresh burst.

## Rule ordering and replies

Repeated echo requests with the same ICMP identifier can belong to a connection that conntrack already treats as ESTABLISHED. If generic ESTABLISHED acceptance runs first, later requests may bypass the limiter. Therefore every matching outside echo request must reach W6_PING before generic stateful acceptance.

The explicit DROP at the end of W6_PING matters. If excess traffic merely returned to FORWARD, it could still be accepted by a later rule. Accepted echo requests produce echo replies from the inside server; the existing stateful reply rule allows those responses. RELATED ICMP errors remain permitted by the baseline stateful rule. We do not add a blanket ACCEPT for all ICMP.

## Check on the course VMs

Generate and apply `--exercise 2` following the lab README. On Kali:

```sh
ping -c 5 -i 1 192.168.2.10
sudo ping -c 20 -i 0.05 192.168.2.10
```

The ordinary ping should normally get replies if the host responds to ICMP. The rapid burst should show loss once available tokens are exhausted. Packet loss alone does not prove rate limiting: compare the W6_PING ACCEPT and DROP counters on the gateway:

```sh
sudo iptables -nvxL W6_PING --line-numbers
```

From Windows, try `ping 192.168.2.10` while Kali sends a burst. Windows has a separate bucket and should still receive permitted replies. Host firewalls and server ICMP settings can affect replies independently of the gateway.

## Recorded outcome

The isolated demonstration observed:

- Ordinary ping: 3 sent, 3 received, 0% loss.
- Kali burst: 20 echo requests; the gateway counters recorded **4 accepted and 16 dropped**. This bucket was partially consumed by the earlier ordinary-ping run, so the observed initial acceptance count need not equal five.
- A Windows stand-in ping still received a reply after Kali's burst.

The burst deliberately reused the ICMP identifier so subsequent requests could be tracked as ESTABLISHED. The observed drops support the chosen ordering. These are Linux namespace observations, not measurements from the supplied course VMs. See `Week 6/Lab/Evidence/namespace-demo.json` in the course repository.

## Limitations

A per-source limit does not cap total traffic from many sources. Spoofed source addresses can spread traffic across buckets; shared NAT can put many legitimate users behind one bucket. Five initial requests may still be too permissive for a very constrained host, or one per second may be too restrictive for some monitoring tools. The chosen values are illustrative and should match the operational need.

## Files and source

- `rules.v4.template`: limiter chain and early forwarding jump.
- `rules.sample.v4`: complete cumulative exercises 1-2 ruleset with the synthetic MAC.
- Assignment: `Exercise-network security.pdf`, page 3, Exercise 2.
