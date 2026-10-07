FROM gcc:14-bookworm
RUN apt-get update && apt-get install -y --no-install-recommends \
    iptables iproute2 iputils-ping python3 python3-scapy netcat-openbsd \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /lab
CMD ["bash"]
