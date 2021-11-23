FROM gradle:7.3.0-jdk17 AS build

COPY gradle /usr/app/gradle
COPY src /usr/app/src
COPY build.gradle.kts /usr/app/
COPY settings.gradle.kts /usr/app/

WORKDIR /usr/app/

RUN gradle build
RUN ls /usr/app/

FROM openjdk:latest

WORKDIR /usr/app/
COPY --from=build /usr/app/build/libs/RadioRythm.jar .

ENTRYPOINT ["java", "-jar", "RadioRythm.jar"]
