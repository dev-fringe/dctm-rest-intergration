FROM openjdk:8-jdk-alpine3.7 AS builder
RUN java -version
RUN apk --no-cache add maven && mvn --version
RUN mvn package

FROM openjdk:8-jre-alpine3.7
COPY target/*.war /app.war
CMD ["/usr/bin/java", "-jar", "-Dspring.profiles.active=LOC", "/app.war"]]