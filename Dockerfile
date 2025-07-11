FROM maven:3.8.1-openjdk-17-slim AS builder
ARG DEBIAN_FRONTEND=noninteractive

USER root
RUN mkdir -p /app/tic-tac-toe-game
WORKDIR /app/tic-tac-toe-game
COPY ./ /app/tic-tac-toe-game/

RUN mvn -Pproduction -Dmaven.test.skip=true clean package vaadin:prepare-frontend -Dvaadin.force.production.build=true

FROM openjdk:17.0.1-jdk-slim
ARG DEBIAN_FRONTEND=noninteractive

USER root
WORKDIR /app

COPY --from=builder /app/tic-tac-toe-game/target/tic-tac-toe-game-0.0.1-SNAPSHOT.jar /app/
EXPOSE 8080/tcp

ENTRYPOINT ["java", "-Dvaadin.productionMode=true", "-jar", "tic-tac-toe-game-0.0.1-SNAPSHOT.jar"]