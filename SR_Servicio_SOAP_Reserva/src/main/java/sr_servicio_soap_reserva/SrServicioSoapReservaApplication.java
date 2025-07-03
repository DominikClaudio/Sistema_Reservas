package sr_servicio_soap_reserva;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.webservices.WebServicesAutoConfiguration;

@SpringBootApplication(exclude = { WebServicesAutoConfiguration.class })
public class SrServicioSoapReservaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SrServicioSoapReservaApplication.class, args);
    }

}
