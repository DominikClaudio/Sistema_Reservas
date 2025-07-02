FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/Sistema_Reservas-0.0.1-SNAPSHOT.jar Sistema_Reservas.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "Sistema_Reservas.jar"]