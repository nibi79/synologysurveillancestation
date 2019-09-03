FROM openjdk:8

RUN apt-get update \
        && apt-get install -y maven \
        && apt-get clean