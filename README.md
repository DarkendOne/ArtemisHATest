# ArtemisHATest
This project exists to test the behavior of Artemis in a replicated-failback-static
configuration given the example linked to below.  Specifically this test exists to functionally test the zero message loss guarantee for durable queues.

https://github.com/apache/activemq-artemis/tree/master/examples/features/ha/replicated-failback-static  

## Overview of test
This test sets up two docker containers based on an image that already has java installed.  It deploys Artemis on both containers with one setup as the master and one as the slave.  The configuration is nearly the same as the one given in the example.  Running on the same machine the communication is so fast between the two docker containers makes it difficult to reliably test the behavior of the replication.  My solution to this problem is to use linux netem to simulate a network delay on the slave.  Once both of the brokers are up, and netem is setup to simulate a network delay between them, the final step of this test is to execute the java test program.  The test program will begin by synchronously and transactionally placing 100 80kB messages on the exampleQueue on the master node.  Once the last message has been successfully placed on the queue, the test program will immediately execute `kill -9` on the artemis master node.  Executing `kill -9` on the server simulates a sudden node failure.  Once the master node goes down, the test program waits a 10 seconds for the slave to come up before trying to connect to it.  Once the test program connects to the slave node it then tried to read the 100 messages from the slave node to make sure they are all there.  


## Requirements
* Docker

## Running the test
## 1 - Execute setupTests.bash to setup the test.
```
./setupTests.bash
```  

