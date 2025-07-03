FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/SR_Servicio_Habitaciones-0.0.1-SNAPSHOT.jar SR_Servicio_Habitaciones.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "SR_Servicio_Habitaciones.jar"]