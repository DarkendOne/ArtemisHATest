#!/bin/bash 

# Compile and deploy Java test program 
cd ./artemisTestHA
mvn clean compile assembly:single
cd ..
cp ./artemisTestHA/target/artemisTestHA-1.0-SNAPSHOT-jar-with-dependencies.jar ./docker/artemis-master/artemisTestHA-1.0-SNAPSHOT-jar-with-dependencies.jar

# Create artemis master and slave images
docker build -t artemis-tests/artemis-master ./docker/artemis-master
docker build -t artemis-tests/artemis-slave ./docker/artemis-slave

