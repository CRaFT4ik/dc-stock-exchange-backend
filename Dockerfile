FROM gradle:7.4.0-jdk17 AS cache
RUN mkdir /gradle_cache
ENV GRADLE_USER_HOME /gradle_cache
WORKDIR /build
COPY gradlew settings.gradle exchange-service/build.gradle ./
RUN gradle dockerResolveDependencies -i --stacktrace --scan

###

FROM cache AS build
ENV GRADLE_USER_HOME /gradle_cache
COPY . .
RUN gradle :exchange-service:bootJar -i --stacktrace

###

FROM openjdk:17
MAINTAINER Eldar Timraleev <eldar.tim@gmail.com>

RUN mkdir app
WORKDIR app
COPY --from=build /build/exchange-service/build/libs/*.jar exchange-service-application.jar

EXPOSE 16480

CMD ["java", "-jar", "exchange-service-application.jar"]
# ENTRYPOINT ["tail", "-f", "/dev/null"]