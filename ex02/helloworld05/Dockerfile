FROM openjdk:17 AS builder
ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} app.jar
RUN java -Djarmode=tools -jar app.jar extract --layers --launcher


FROM openjdk:17.0.2-jdk-slim
COPY --from=builder app/dependencies/ ./
COPY --from=builder app/snapshot-dependencies ./
COPY --from=builder app/spring-boot-loader ./
COPY --from=builder app/application ./

RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

ENTRYPOINT ["java", "-cp", ".", "org.springframework.boot.loader.JarLauncher"]
