#!/bin/bash

printf "Implementing network latency simulation\n"
tc qdisc add dev eth0 root netem delay 500ms

printf "Starting slave node\n"
./server/bin/artemis-service start

printf "Waiting for server to start\n"
sleep 30

printf "Done starting slave node.\n"
