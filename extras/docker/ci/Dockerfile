#Download base image ubuntu 16.04
FROM mauriciojost/scala:latest

COPY sources.list /etc/apt/sources.list                                                                                                       
RUN apt-get update; exit 0                                                                                                                    
RUN apt-get install -y graphviz xvfb openjfx

