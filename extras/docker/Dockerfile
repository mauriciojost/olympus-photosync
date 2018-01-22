#Download base image ubuntu 16.04
FROM mauriciojost/scala:latest as builder
 
RUN apt-get update
 
RUN apt-get install -y graphviz

RUN apt-get install -y openjfx

FROM builder as build

ADD . .

RUN ./extras/packager/build.sh

FROM openjdk:9-jre-slim

COPY --from=builder target/olympus-photosync-0.13-SNAPSHOT/usr/share/olympus-photosync /photosync

ENV PATH /photosync/bin:$PATH
ENTRYPOINT ["olympus-photosync"]
