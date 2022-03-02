FROM gradle:7.4.0-jdk11-alpine AS cache
RUN mkdir /gradle_cache
ENV GRADLE_USER_HOME /gradle_cache
WORKDIR /build
COPY settings.gradle gradlew \
    exchange-service/build.gradle exchange-service/settings.gradle\
    ./
RUN gradle clean build -i --stacktrace -x bootJar

###

FROM cache AS build
ENV GRADLE_USER_HOME /gradle_cache
COPY . .
RUN gradle bootJar -i --stacktrace

###

FROM openjdk:12-alpine
MAINTAINER Eldar Timraleev <eldar.tim@gmail.com>

RUN mkdir app
COPY --from=build /build/exchange-service/build/libs/*.jar /app/exchange-service-application.jar
WORKDIR app

EXPOSE 16480

CMD ["java", "-jar", "exchange-service-application.jar"]
# ENTRYPOINT ["tail", "-f", "/dev/null"]