package sistema_reservas.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class NotificaciónController {

    @MessageMapping("/reserva")
    @SendTo("/tem/notificacion")
    public String enviarNotificacion(String mensaje){
        return mensaje;
    }
}
