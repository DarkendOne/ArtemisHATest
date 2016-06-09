FROM java:8-jdk
RUN mkdir /opt/artemis
WORKDIR /opt/artemis
RUN apt-get update
RUN apt-get -y install wget unzip vim 
ADD http://www.apache.org/dyn/closer.cgi?filename=activemq/activemq-artemis/1.2.0/apache-artemis-1.2.0-bin.zip&action=download /opt/artemis/apache-artemis-1.2.0-bin.zip
#ADD ./apache-artemis-1.2.0-bin.zip /opt/artemis
ADD ./keepalive.sh /opt/artemis
RUN unzip apache-artemis-1.2.0-bin.zip
RUN ./apache-artemis-1.2.0/bin/artemis create --allow-anonymous \
    --cluster-password password \
    --cluster-user cluster-user \
    --failover-on-shutdown \
    --password password \
    --queues jms.queue.TestQueue \
    --replicated \
    --role admin \
    --silent \
    --user admin \
    /opt/artemis/server

ADD http://apache.mirrors.pair.com/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.zip /opt/artemis/apache-maven-3.3.9-bin.zip
RUN unzip apache-maven-3.3.9-bin.zip
