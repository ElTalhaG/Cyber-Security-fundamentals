#!/usr/bin/env python3
"""Run the three lab exercises in disposable Linux network namespaces."""
import json
import subprocess
import sys
import time
from pathlib import Path

HERE = Path(__file__).resolve().parent
NS = {role: "w6_" + role for role in ("gateway", "windows", "kali", "server")}
MAC = "02:00:00:00:01:12"  # Synthetic, not the course Windows VM's measured MAC.
observations = []
services = []


def run(*args, namespace=None, check=True, input=None):
    command = list(args)
    if namespace:
        command = ["ip", "netns", "exec", NS[namespace], *command]
    result = subprocess.run(command, text=True, input=input, capture_output=True, timeout=20)
    if check and result.returncode:
        raise RuntimeError(f"{command}: {result.stderr.strip()}")
    return result


def observe(name, passed, detail):
    observations.append({"scenario": name, "passed": bool(passed), "detail": detail})
    if not passed:
        raise RuntimeError(f"Unexpected result: {name}: {detail}")


def setup():
    for name in NS.values():
        run("ip", "netns", "add", name)
    run("ip", "link", "add", "w6_outside", "type", "bridge")
    run("ip", "link", "set", "w6_outside", "up")
    for index, (role, address) in enumerate([
            ("gateway", "192.168.1.1/24"),
            ("windows", "192.168.1.12/24"),
            ("kali", "192.168.1.11/24")]):
        host, peer = f"w6_h{index}", f"w6_p{index}"
        run("ip", "link", "add", host, "type", "veth", "peer", "name", peer)
        run("ip", "link", "set", host, "master", "w6_outside")
        run("ip", "link", "set", host, "up")
        run("ip", "link", "set", peer, "netns", NS[role])
        run("ip", "link", "set", peer, "name", "eth0", namespace=role)
        if role == "windows":
            run("ip", "link", "set", "eth0", "address", MAC, namespace=role)
        run("ip", "addr", "add", address, "dev", "eth0", namespace=role)
        run("ip", "link", "set", "eth0", "up", namespace=role)
    run("ip", "link", "add", "w6_inside", "type", "veth", "peer", "name", "w6_server")
    for role, old, interface, address in [
            ("gateway", "w6_inside", "eth1", "192.168.2.1/24"),
            ("server", "w6_server", "eth0", "192.168.2.10/24")]:
        run("ip", "link", "set", old, "netns", NS[role])
        run("ip", "link", "set", old, "name", interface, namespace=role)
        run("ip", "addr", "add", address, "dev", interface, namespace=role)
        run("ip", "link", "set", interface, "up", namespace=role)
    for role in NS:
        run("ip", "link", "set", "lo", "up", namespace=role)
    for role in ("windows", "kali"):
        run("ip", "route", "add", "192.168.2.0/24", "via", "192.168.1.1", namespace=role)
    run("ip", "route", "add", "192.168.1.0/24", "via", "192.168.2.1", namespace="server")
    run("sh", "-c", "mount -t proc proc /proc && sysctl -w net.ipv4.ip_forward=1",
        namespace="gateway")


def start_service(role, port):
    code = """import socket, sys
s = socket.socket()
s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
s.bind(('0.0.0.0', int(sys.argv[1])))
s.listen(10)
print('ready', flush=True)
while True:
    c, _ = s.accept()
    try:
        c.sendall(b'week6-demo\\n')
    except OSError:
        pass
    c.close()
"""
    process = subprocess.Popen(["ip", "netns", "exec", NS[role], "python3", "-u", "-c", code, str(port)],
                               stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
    services.append(process)
    if process.stdout.readline().strip() != "ready":
        raise RuntimeError("Demo TCP service did not start")


def connect(role, address, port):
    code = """import socket, sys
try:
    with socket.create_connection((sys.argv[1], int(sys.argv[2])), timeout=1) as s:
        assert s.recv(64) == b'week6-demo\\n'
except (OSError, AssertionError):
    sys.exit(1)
"""
    return run("python3", "-c", code, address, str(port), namespace=role, check=False).returncode == 0


def apply(exercise, short_cooldown=False):
    rules = run("python3", str(HERE / "render_rules.py"), "--exercise", str(exercise),
                "--windows-mac", MAC).stdout
    if short_cooldown:
        rules = rules.replace("--seconds 300", "--seconds 3").replace("--seconds 60", "--seconds 2")
    run("iptables-restore", "--test", namespace="gateway", input=rules)
    run("iptables-restore", namespace="gateway", input=rules)


def ping(role, count=1, interval="1"):
    return run("ping", "-n", "-c", str(count), "-i", interval, "-W", "1", "192.168.2.10",
               namespace=role, check=False)


def burst(role, count=20, tcp=False):
    # Reuse ICMP ID deliberately: later requests can be ESTABLISHED in conntrack.
    protocol = ("[IP(dst='192.168.2.10')/TCP(sport=41000+i,dport=4000+i,flags='S') "
                f"for i in range({count})]" if tcp else
                f"[IP(dst='192.168.2.10')/ICMP(id=4242,seq=i) for i in range({count})]")
    run("python3", "-c", "from scapy.all import IP, ICMP, TCP, send; send(" + protocol +
        ", inter=0, verbose=False)", namespace=role)


def ping_counters():
    text = run("iptables", "-nvxL", "W6_PING", namespace="gateway").stdout
    counts = {}
    for line in text.splitlines():
        columns = line.split()
        if len(columns) > 2 and columns[2] in ("ACCEPT", "DROP"):
            counts[columns[2]] = int(columns[0])
    return counts


def banned():
    return run("cat", "/proc/net/xt_recent/W6_BANNED", namespace="gateway").stdout


def scenarios():
    apply(1)
    observe("E1: internal TCP connection and response", connect("server", "192.168.1.11", 8080),
            "Internal server received the external TCP banner.")
    observe("E1: Windows Telnet port with matching MAC", connect("windows", "192.168.2.10", 23),
            "Linux Windows stand-in received TCP/23 banner; not a Telnet login test.")
    observe("E1: Kali Telnet denied", not connect("kali", "192.168.2.10", 23), "TCP/23 timed out.")
    observe("E1: Windows unrelated port denied", not connect("windows", "192.168.2.10", 22),
            "An active TCP/22 listener was unreachable.")
    run("ip", "link", "set", "eth0", "address", "02:00:00:00:01:99", namespace="windows")
    observe("E1: correct Windows IP with wrong MAC denied", not connect("windows", "192.168.2.10", 23),
            "Changing the source NIC MAC blocked a new TCP/23 session.")
    run("ip", "link", "set", "eth0", "address", MAC, namespace="windows")
    observe("E1: outside echo requests denied", ping("kali").returncode != 0, "No ICMP exception in stage 1.")
    observe("E1: gateway INPUT remains closed", run("ping", "-c", "1", "-W", "1", "192.168.1.1",
            namespace="kali", check=False).returncode != 0, "Forwarding permissions do not open gateway INPUT.")

    apply(2)
    result = ping("kali", 3)
    observe("E2: ordinary outside ping allowed", result.returncode == 0, result.stdout.strip())
    run("iptables", "-Z", "W6_PING", namespace="gateway")
    burst("kali")
    counts = ping_counters()
    observe("E2: burst exceeds per-source rate", counts.get("DROP", 0) > 0 and counts.get("ACCEPT", 0) > 0,
            counts)
    before = counts["ACCEPT"]
    result = ping("windows")
    after = ping_counters()["ACCEPT"]
    observe("E2: second source has its own bucket", result.returncode == 0 and after > before,
            "Windows stand-in received a reply after the Kali burst.")

    apply(3)
    burst("kali", 10, tcp=True)
    observe("E3: first ten SYN packets do not ban", "192.168.1.11" not in banned(), "10 SYNs in 60 seconds.")
    # A distinct port/source-port pair avoids relying on retransmission accounting.
    run("python3", "-c", "from scapy.all import IP,TCP,send; send(IP(dst='192.168.2.10')/"
        "TCP(sport=42000,dport=4100,flags='S'),verbose=False)", namespace="kali")
    observe("E3: eleventh SYN creates ban", "192.168.1.11" in banned(), banned().strip())
    observe("E3: banned source ping denied", ping("kali").returncode != 0,
            "Ban precedes both ICMP acceptance and established-flow acceptance.")
    observe("E3: other source remains allowed", connect("windows", "192.168.2.10", 23),
            "Windows TCP/23 still works.")
    # Rebuild only the disposable gateway's lists and use a shortened demonstration interval.
    run("iptables-restore", namespace="gateway",
        input="*filter\n:INPUT ACCEPT [0:0]\n:FORWARD ACCEPT [0:0]\n:OUTPUT ACCEPT [0:0]\nCOMMIT\n")
    apply(3, short_cooldown=True)
    burst("kali", 11, tcp=True)
    observe("E3: shortened demo ban activates", "192.168.1.11" in banned(),
            "Demo only: 2-second attempt window / 3-second ban; saved rules use 60/300.")
    time.sleep(4)
    observe("E3: source works after idle ban expires", ping("kali").returncode == 0,
            "4 idle seconds elapsed in the shortened demo; 300-second production interval not waited out.")


def main():
    report = {"environment": "Disposable Docker Linux namespaces, amd64 on Apple Silicon",
              "windows": "Linux namespace stand-in with synthetic MAC " + MAC,
              "telnet": "TCP/23 banner service, not Metasploitable authentication",
              "observations": observations}
    try:
        report["kernel"] = run("uname", "-r").stdout.strip()
        report["iptables"] = run("iptables", "-V").stdout.strip()
        setup()
        start_service("kali", 8080)
        start_service("server", 23)
        start_service("server", 22)
        scenarios()
        report["status"] = "completed"
        report["shortened_cooldown_final_rules"] = run("iptables-save", namespace="gateway").stdout
    except Exception as error:
        report["status"] = "failed"
        report["error"] = str(error)
    finally:
        for process in services:
            process.terminate()
            try:
                process.wait(timeout=2)
            except subprocess.TimeoutExpired:
                process.kill()
                process.wait()
        for name in NS.values():
            run("ip", "netns", "del", name, check=False)
        run("ip", "link", "del", "w6_outside", check=False)
    print(json.dumps(report, indent=2))
    return 0 if report["status"] == "completed" else 1


if __name__ == "__main__":
    sys.exit(main())
