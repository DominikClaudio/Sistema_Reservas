FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/SR_Servicio_Correos-0.0.1-SNAPSHOT.jar SR_Servicio_Correos.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "SR_Servicio_Correos.jar"]