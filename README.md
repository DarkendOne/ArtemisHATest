# ArtemisHATest
This project exists to test the behavior of Artemis in a replicated-failback-static
configuration.  Given the example
https://github.com/apache/activemq-artemis/tree/master/examples/features/ha/replicated-failback-static
## Prequirements
* Java 8
* Maven 

## Running the test
## 1 - Compile the Java Test Program
```
cd ./artemisTestHA
mvn clean compile assembly:single
cp ./artemisTestHA/target/artemisTestHA-1.0-SNAPSHOT-jar-with-dependencies.jar ./docker/artemis-master/artemisTestHA-1.0-SNAPSHOT-jar-with-dependencies.jar
```  

## 2 - Build artemis-master image
docker build -t artemis-tests/artemis-master .

## 3 - Build artemis-slave image
docker build -t artemis-tests/artemis-slave .

## 4 - Create an artemis-master container from the artemis-master image.
`docker run -it --rm --name artemis-master --hostname=artemis-master artemis-tests/artemis-master /bin/bash`

## 4 - Create an artemis-slave container from the artemis-slave image.
`docker run -it --rm --privileged=true --name artemis-slave --hostname=artemis-slave --link artemis-master:artemis-master artemis-tests/artemis-slave /bin/bash`

Execute the start.sh in the artemis-master container
`./start.sh`

Execute the start.sh script in the artemis-slave container
`./start.sh`

Execute the Java test program
`java -jar ./artemisTestHA-1.0-SNAPSHOT-jar-with-dependencies.jar`
