FROM maven:3.8.3-openjdk-17 AS build

COPY src /usr/app/src
COPY pom.xml /usr/app/
RUN mvn -f /usr/app/pom.xml clean package

FROM openjdk:latest

WORKDIR /usr/app/
COPY --from=build /usr/app/target/springSwansBot.jar .

ENTRYPOINT ["java", "-jar", "springSwansBot.jar"]
