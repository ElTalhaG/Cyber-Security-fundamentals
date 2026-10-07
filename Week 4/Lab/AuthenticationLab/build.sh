#!/bin/bash
set -euo pipefail
cd "$(dirname "$0")"
mkdir -p out
sources=()
while IFS= read -r -d '' file; do
    sources+=("$file")
done < <(find . -type f -name '*.java' -not -path './out/*' -print0)
javac -d out "${sources[@]}"
