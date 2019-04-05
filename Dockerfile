FROM java:8-jdk-alpine

COPY ./target/spring-boot-metrics-logger.jar /usr/app/App.jar

WORKDIR /usr/app

RUN sh -c 'touch App.jar'

EXPOSE 80

ENTRYPOINT ["java","-jar","App.jar"]
