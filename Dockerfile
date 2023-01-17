FROM openjdk:18-jdk-oracle

RUN mkdir /app
COPY ./target/eth-connector-0.0.1-SNAPSHOT.jar /app
WORKDIR /app

ENTRYPOINT java -jar eth-connector-0.0.1-SNAPSHOT.jar
