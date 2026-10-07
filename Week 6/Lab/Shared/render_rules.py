#!/usr/bin/env python3
"""Render a complete IPv4 filter ruleset; never apply firewall changes."""
import argparse
import ipaddress
import re
import string
from pathlib import Path

LAB = Path(__file__).resolve().parent.parent
EXERCISES = {
    1: "Exercise 1 - Default Policy",
    2: "Exercise 2 - ICMP Filtering",
    3: "Exercise 3 - Port Scanning Defense",
}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--exercise", type=int, choices=EXERCISES, required=True)
    parser.add_argument("--windows-mac", required=True,
                        help="Actual Windows NIC MAC; use 02:00:00:00:01:12 only in the demo")
    parser.add_argument("--outside-interface", default="eth0")
    parser.add_argument("--inside-interface", default="eth1")
    args = parser.parse_args()
    if not re.fullmatch(r"(?:[0-9a-fA-F]{2}:){5}[0-9a-fA-F]{2}", args.windows_mac):
        parser.error("Windows MAC must have six colon-separated hexadecimal bytes")
    first_byte = int(args.windows_mac[:2], 16)
    if first_byte & 1 or args.windows_mac.lower() == "00:00:00:00:00:00":
        parser.error("Windows MAC must be a nonzero unicast address")
    for interface in [args.outside_interface, args.inside_interface]:
        if not re.fullmatch(r"[A-Za-z0-9_.:-]{1,15}", interface):
            parser.error("Invalid interface name")
    if args.outside_interface == args.inside_interface:
        parser.error("Inside and outside interfaces must differ")
    values = {
        "OUTSIDE": args.outside_interface,
        "INSIDE": args.inside_interface,
        "WINDOWS_MAC": args.windows_mac.lower(),
        "OUTSIDE_NET": str(ipaddress.ip_network("192.168.1.0/24")),
        "INSIDE_NET": str(ipaddress.ip_network("192.168.2.0/24")),
        "WINDOWS_IP": "192.168.1.12",
        "SERVER_IP": "192.168.2.10",
    }
    declarations = [":INPUT DROP [0:0]", ":FORWARD DROP [0:0]", ":OUTPUT ACCEPT [0:0]"]
    rules = ["-A INPUT -i lo -j ACCEPT",
             "-A INPUT -m conntrack --ctstate INVALID -j DROP",
             "-A INPUT -m conntrack --ctstate ESTABLISHED,RELATED -j ACCEPT",
             "-A FORWARD -m conntrack --ctstate INVALID -j DROP",
             "-A FORWARD -i $OUTSIDE ! -s $OUTSIDE_NET -j DROP",
             "-A FORWARD -i $INSIDE ! -s $INSIDE_NET -j DROP"]
    # Later exercises are inserted before the baseline established-flow ACCEPT.
    # Otherwise repeated echo requests could bypass the per-source limiter.
    for exercise in range(args.exercise, 1, -1):
        path = LAB / EXERCISES[exercise] / "rules.v4.template"
        lines = path.read_text().splitlines()
        declarations.extend(line for line in lines if line.startswith(":"))
        rules.extend(line for line in lines if line.startswith("-A "))
    rules.extend(line for line in (LAB / EXERCISES[1] / "rules.v4.template").read_text().splitlines()
                 if line.startswith("-A "))
    text = "\n".join(["# Generated IPv4 lab filter table; replaces existing filter rules.",
                      "*filter", *declarations, *rules, "COMMIT", ""])
    print(string.Template(text).substitute(values), end="")


if __name__ == "__main__":
    main()
