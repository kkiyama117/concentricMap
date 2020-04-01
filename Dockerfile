FROM openjdk:8-jre-alpine

ENV APPLICATION_USER ktor
RUN apk add curl wget
RUN adduser -D -g '' $APPLICATION_USER

RUN mkdir /app
RUN chown -R $APPLICATION_USER /app

USER $APPLICATION_USER

ADD get_jar.sh /app/get_jar.sh
WORKDIR /app
RUN  /app/get_jar.sh
RUN chmod 755 /app/app.jar

CMD ["java", "-server", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-XX:InitialRAMFraction=2", "-XX:MinRAMFraction=2", "-XX:MaxRAMFraction=2", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=100", "-XX:+UseStringDeduplication", "-jar", "app.jar"]