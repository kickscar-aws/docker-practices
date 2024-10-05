FROM openjdk:17 AS builder
ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} app.jar
RUN mkdir -p target/dependency

WORKDIR /target/dependency
RUN jar -xf ../../*.jar

FROM openjdk:17.0.2-jdk-slim
ARG DEPENDENCY=/target/dependency
COPY --from=builder ${DEPENDENCY}/BOOT-INF/classes /app
COPY --from=builder ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=builder ${DEPENDENCY}/META-INF /app/META-INF

RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

ENTRYPOINT ["java","-cp","app:app/lib/*","helloworld03.HelloworldApplication"]

