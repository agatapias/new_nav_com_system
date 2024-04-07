FROM maven:3.9.4-eclipse-temurin-17
WORKDIR /build/
COPY pom.xml .
COPY ./src ./src
RUN mvn clean install -Dmaven.test.skip=true
ENTRYPOINT ["java", "-jar", "/build/target/ships-0.0.1-SNAPSHOT.jar"]