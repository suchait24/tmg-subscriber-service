FROM adoptopenjdk/openjdk11:alpine-jre

# maintainer info
LABEL maintainer="suchait.gaurav.ctr@sabre.com"

# add volume pointing to /tmp
VOLUME /tmp

# Make port 9000 available to the world outside the container
EXPOSE 9000

# application jar file when packaged
ARG jar_file=target/consumer-service.jar

# add application jar file to container
COPY ${jar_file} consumer-service.jar

# run the jar file
ENTRYPOINT ["java", "-jar", "consumer-service.jar"]