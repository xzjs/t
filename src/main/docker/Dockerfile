FROM nathonfowlie/centos-jre
VOLUME /tmp
ADD test-0.1.0.jar app.jar
ADD libmsc64.so /lib64/libmsc64.so
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./uexitrandom -jar /tmp/app.jar" ]