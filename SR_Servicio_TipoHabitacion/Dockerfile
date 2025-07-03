FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/SR_Servicio_TipoHabitacion-0.0.1-SNAPSHOT.jar SR_Servicio_TipoHabitacion.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "SR_Servicio_TipoHabitacion.jar"]