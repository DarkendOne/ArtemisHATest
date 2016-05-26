#!/bin/sh

echo "172.17.0.3  artemis-slave" >> /etc/hosts
/opt/artemis/server/bin/artemis-service start
