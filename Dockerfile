FROM maven:3.8.6-openjdk-8 AS dependencies

WORKDIR /opt/app
COPY pom.xml .
COPY libs /opt/app/libs

RUN mvn -B -e org.apache.maven.plugins:maven-dependency-plugin:3.1.2:go-offline

FROM maven:3.8.6-openjdk-8 AS builder

WORKDIR /opt/app

COPY --from=dependencies /root/.m2 /root/.m2
COPY --from=dependencies /opt/app/ /opt/app

COPY src /opt/app/src

RUN mvn -B -e clean install -DskipTests

FROM openjdk:8-jre-alpine3.7

COPY --from=builder /opt/app/target/*.war app.war

COPY *.did .
COPY *.did.wallet .
COPY verifyConfig.* .

EXPOSE 8280

ENTRYPOINT [                                                \
    "java",                                                 \
    "-jar",                                                 \
    "-server",                                              \
    "-XX:+UseG1GC",                                         \
    "-XX:+DisableExplicitGC",                               \
    "-Xms2048m",                                               \
    "-Xmx8192m",                                               \
    "-XX:MetaspaceSize=512m",                               \
    "-XX:MaxMetaspaceSize=2048m",                              \
    "app.war"              \
]