FROM alpine:3.11.6
ENV JAVA_HOME=/usr/lib/jvm/default-jvm/jre
ENV TASK_DIRECTORY=/
WORKDIR /
COPY src/ /
COPY groovy-3.0.4 /usr/local/groovy-3.0.4/
RUN ["sh", "/ContainerScript.sh"]
WORKDIR /home
CMD ["ttyd", "-p", "8084", "task"]

