#!/bin/sh
set -eu

cd "$(dirname "$0")"
mkdir -p build/classes
find src/main/java -name '*.java' -print > build/sources.list
javac --release 17 -d build/classes @build/sources.list
java -cp build/classes lab.protocol.nsl.NslDemo
