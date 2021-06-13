FROM adoptopenjdk/openjdk11:alpine-jre

# maintainer info
LABEL maintainer="suchait.gaurav.ctr@sabre.com"

# add volume pointing to /tmp
VOLUME /tmp

# Make port 8090 available to the world outside the container
EXPOSE 8090

# application jar file when packaged
ARG jar_file=target/tmg-subscriber-service.jar

# add application jar file to container
COPY ${jar_file} tmg-subscriber-service.jar

# run the jar file
ENTRYPOINT ["java", "-jar", "tmg-subscriber-service.jar"]