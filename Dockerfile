FROM java:8-jdk-alpine

COPY ./target/spring-boot-metrics-logger.jar /usr/app/app.jar

WORKDIR /usr/app

RUN sh -c 'app.jar'

ENTRYPOINT ["java","-jar","app.jar"]