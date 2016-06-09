#!/bin/sh

printf "Installing artemis-slave address into /etc/hosts\n"
echo "172.17.0.3  artemis-slave" >> /etc/hosts

printf "Start master node.\n"
/opt/artemis/server/bin/artemis-service start

printf "Waiting 20 seconds for master to start.\n"
sleep 20

printf "Done starting master node.\n"

