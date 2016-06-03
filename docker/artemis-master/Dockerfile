FROM artemis-tests/artemis-base

ADD ./artemisTestHA /opt/artemis/artemisTestHA
RUN ./apache-maven-3.3.9/bin/mvn -f /opt/artemis/artemisTestHA/pom.xml clean compile assembly:single 
ADD ./broker.xml /opt/artemis/server/etc/
ADD ./start.sh /opt/artemis/
ADD ./runtest.sh /opt/artemis/
