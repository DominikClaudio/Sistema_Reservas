package com.example.microservicio_notificaciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MicroservicioNotificacionesSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroservicioNotificacionesSpringBootApplication.class, args);
	}

}
