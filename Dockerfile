FROM eclipse-temurin:17-jre-alpine as builder
WORKDIR application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM eclipse-temurin:17-jre-alpine
RUN apk add --no-cache jattach

ARG USER_NAME=demouser
ARG GROUP_NAME=demogroup
RUN addgroup -g 1001 -S $GROUP_NAME \
    && adduser -u 1000 -S $USER_NAME -G $GROUP_NAME
USER $USER_NAME
WORKDIR application

COPY --from=builder --chown=$USER_NAME:$GROUP_NAME application/dependencies/ ./
COPY --from=builder --chown=$USER_NAME:$GROUP_NAME application/spring-boot-loader/ ./
COPY --from=builder --chown=$USER_NAME:$GROUP_NAME application/snapshot-dependencies/ ./
COPY --from=builder --chown=$USER_NAME:$GROUP_NAME application/application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
