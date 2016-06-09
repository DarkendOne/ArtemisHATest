#!/bin/bash 

printf "Create artemis base, master, and slave images.\n"
docker build -t artemis-tests/artemis-base ./docker/artemis-base
docker build -t artemis-tests/artemis-master ./docker/artemis-master
docker build -t artemis-tests/artemis-slave ./docker/artemis-slave

printf "Starting docker-master container.\n"
docker run -d --name artemis-master --hostname=artemis-master artemis-tests/artemis-master /bin/bash ./keepalive.sh

printf "Starting docker-slave container.\n"
docker run -d --privileged=true --name artemis-slave --hostname=artemis-slave --link artemis-master:artemis-master artemis-tests/artemis-slave /bin/bash ./keepalive.sh

printf "Running start.sh on artemis-master.\n"
docker exec -it artemis-master /bin/bash ./start.sh

printf "Running start.sh on artemis-slave.\n"
docker exec -it artemis-slave /bin/bash ./start.sh

printf "Executing java replication test.\n"
docker exec -it artemis-master /usr/bin/java -jar ./artemisTestHA/target/artemisTestHA-1.0-SNAPSHOT-jar-with-dependencies.jar

printf "Cleaning up.\n"
printf "Stopping artemis-master.\n"
docker stop artemis-master

printf "Stopping artemis-slave.\n"
docker stop artemis-slave

printf "Removing artemis-master.\n"
docker rm artemis-master

printf "Removing artemis-slave.\n"
docker rm artemis-slave

