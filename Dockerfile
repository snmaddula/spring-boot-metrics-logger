FROM java:8-jdk-alpine

COPY ./target/basic-boot-app.jar /usr/app/

WORKDIR /usr/app

RUN sh -c 'touch basic-boot-app.jar'

ENTRYPOINT ["java","-jar","basic-boot-app.jar"]