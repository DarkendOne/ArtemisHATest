#!/bin/bash 

# Create artemis master and slave images
docker build -t artemis-tests/artemis-base ./docker/artemis-base
docker build -t artemis-tests/artemis-master ./docker/artemis-master
docker build -t artemis-tests/artemis-slave ./docker/artemis-slave

