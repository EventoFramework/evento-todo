# syntax=docker/dockerfile:1.7
FROM eclipse-temurin:25-jdk-noble AS build
WORKDIR /app
COPY gradlew gradlew.bat settings.gradle build.gradle ./
COPY gradle ./gradle
# Normalize line endings in case the build context comes from a Windows checkout
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew
RUN ./gradlew --no-daemon dependencies > /dev/null || true
COPY src ./src
RUN ./gradlew --no-daemon bootJar

# CVE-2026-114938 (Java SE compiler, CVSS 8.1): fixed in Temurin 25.0.5.1+2 (2026-11-18).
# The eclipse-temurin images do not carry it yet, so the runtime is replaced with Adoptium's
# x64 JRE, checked by SHA-256. Drop this stage once 25-jre-noble carries it.
FROM eclipse-temurin:25-jre-noble AS jre
ADD --checksum=sha256:2edef9e4f81ec5ee426d023336d4cfbd7b4644e99884e7a73a6a867b70b17acf https://github.com/adoptium/temurin25-binaries/releases/download/jdk-25.0.5.1%2B2/OpenJDK25U-jre_x64_linux_hotspot_25.0.5.1_2.tar.gz /tmp/jre.tar.gz
RUN mkdir /jre && tar -xzf /tmp/jre.tar.gz -C /jre --strip-components=1

FROM eclipse-temurin:25-jre-noble
RUN rm -rf /opt/java/openjdk
COPY --from=jre /jre /opt/java/openjdk
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
