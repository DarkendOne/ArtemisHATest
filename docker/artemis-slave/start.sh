#!/bin/bash

tc qdisc add dev eth0 root netem delay 500ms
./server/bin/artemis run
