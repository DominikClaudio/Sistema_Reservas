package sistema_reservas.service;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sistema_reservas.config.RabbitConfig;
import sistema_reservas.dto.DetalleCorreoDto;

@Service
public class CorreoProducerService {
    @Autowired
    private AmqpTemplate amqpTemplate;

    public void enviarCorreoReserva(DetalleCorreoDto dto) {
        amqpTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.ROUTING_KEY,
                dto
        );
        System.out.println("Reserva enviada a la cola: " + dto);
    }
}
