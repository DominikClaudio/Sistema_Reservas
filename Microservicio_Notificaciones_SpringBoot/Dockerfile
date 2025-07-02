FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/Microservicio_Notificaciones_SpringBoot-0.0.1-SNAPSHOT.jar Microservicio_Notificaciones_SpringBoot.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "Microservicio_Notificaciones_SpringBoot.jar"]