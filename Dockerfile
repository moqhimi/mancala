FROM openjdk:8
VOLUME /tmp
COPY target/mancala-0.0.1-SNAPSHOT.jar /app.jar
RUN sh -c 'touch /app.jar'
EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["java", "-jar", "/app.jar" ]
