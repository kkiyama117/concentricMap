FROM openjdk:8-jre-alpine

ENV APPLICATION_USER ktor
RUN apk add curl wget
# RUN adduser -D -g '' $APPLICATION_USER

RUN mkdir /app
RUN chown -R root:root /app

# USER $APPLICATION_USER

ADD get_jar.sh /app/get_jar.sh
WORKDIR /app
RUN  /app/get_jar.sh
RUN chown root:root /app/app.jar

CMD ["java", "-server", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-XX:InitialRAMFraction=2", "-XX:MinRAMFraction=2", "-XX:MaxRAMFraction=2", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=100", "-XX:+UseStringDeduplication", "-jar", "app.jar"]