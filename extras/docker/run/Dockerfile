# REMARK: https://github.com/hseeberger/scala-sbt/blob/master/Dockerfile
FROM openjdk:8u151 as scala-sbt

# Env variables
ENV SCALA_VERSION 2.12.4
ENV SBT_VERSION 1.1.0

# Install Scala
## Piping curl directly in tar
RUN curl -fsL https://downloads.typesafe.com/scala/$SCALA_VERSION/scala-$SCALA_VERSION.tgz | tar xfz - -C /root/ && \
    echo >> /root/.bashrc && \
    echo "export PATH=~/scala-$SCALA_VERSION/bin:$PATH" >> /root/.bashrc

# Install sbt
RUN curl -sL -o sbt-$SBT_VERSION.deb https://dl.bintray.com/sbt/debian/sbt-$SBT_VERSION.deb && \
    dpkg -i sbt-$SBT_VERSION.deb && \
    rm sbt-$SBT_VERSION.deb && \
    apt-get update && \
    apt-get install sbt && \
    sbt sbtVersion

# Define working directory
WORKDIR /root

FROM scala-sbt as builder

RUN apt-get update
RUN apt-get install -y --no-install-recommends graphviz \
    fakeroot \
    openjfx \
    rpm

FROM builder as build

ADD . .

RUN sbt debian:packageBin

FROM openjdk:9-jre-slim

COPY --from=build /root/target/olympus-photosync-1master/usr/share/olympus-photosync /photosync

ENV PATH /photosync/bin:$PATH
ENTRYPOINT ["olympus-photosync"]
