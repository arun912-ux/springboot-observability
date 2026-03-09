
# COPYING JAR
#
#FROM eclipse-temurin:17-jre-alpine
#
#WORKDIR /app
#
#EXPOSE 8080

#COPY ./build/libs/org.example-spring-observability-0.0.1-SNAPSHOT.jar /app/app.jar
#
#ENTRYPOINT ["java", "-jar", "/app/app.jar"]
#


#FROM eclipse-temurin:17-jdk-alpine
#
#WORKDIR /app
#
#COPY . /app
#
#RUN ./gradlew clean build
#
#COPY ./build/libs/*.jar ./app.jar
#
#EXPOSE 8080
#
#ENTRYPOINT ["java", "-jar", "/app/spring-observability/app.jar"]

# image creation techniques

# local build -> jar -> copy to image                                   140MB
# container build -> run jar                                            586MB
# container build -> copy to another container -> multi-level builds


## PIPELINES

FROM eclipse-temurin:17-jdk-alpine AS BUILD

WORKDIR app

COPY . /app

RUN ./gradlew clean build -x test


FROM eclipse-temurin:17-jre-alpine

WORKDIR app

COPY --from=BUILD /app/build/libs/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]


