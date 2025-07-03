package sr_servicio_correos.consumer;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import sr_servicio_correos.config.RabbitConfig;
import sr_servicio_correos.dto.DetalleCorreoDto;

@Component
public class CorreoConsumer {

    @Autowired
    private JavaMailSender mailSender;

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void recibirCorreo(DetalleCorreoDto dto) {
        try {
            String asunto = "Reserva confirmada: " + dto.getCodigoReserva();
            String cuerpoHtml = generarContenidoCorreoHtml(dto);

            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(dto.getCorreoDestino());
            helper.setSubject(asunto);
            helper.setText(cuerpoHtml, true); // true para indicar HTML

            mailSender.send(mensaje);
            System.out.println("Correo enviado a " + dto.getCorreoDestino());

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private String generarContenidoCorreoHtml(DetalleCorreoDto d) {
        return """
        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #ddd; border-radius: 10px; padding: 20px;">
            <h2 style="color: #2b5797;">Hola, %s</h2>
            <p style="font-size: 15px;">Tu reserva <strong>%s</strong> ha sido <span style="color: green;">confirmada</span>.</p>

            <table style="width: 100%%; border-collapse: collapse; margin-top: 15px;">
                <tr style="background-color: #f2f2f2;">
                    <th style="text-align: left; padding: 8px;">Detalle</th>
                    <th style="text-align: left; padding: 8px;">Valor</th>
                </tr>
                <tr><td style="padding: 8px;">N° Habitación</td><td style="padding: 8px;">%s</td></tr>
                <tr><td style="padding: 8px;">Tipo</td><td style="padding: 8px;">%s</td></tr>
                <tr><td style="padding: 8px;">Piso</td><td style="padding: 8px;">%s</td></tr>
                <tr><td style="padding: 8px;">Check-in</td><td style="padding: 8px;">%s</td></tr>
                <tr><td style="padding: 8px;">Check-out</td><td style="padding: 8px;">%s</td></tr>
                <tr><td style="padding: 8px;">Precio por día</td><td style="padding: 8px;">S/ %.2f</td></tr>
                <tr><td style="padding: 8px;">Días</td><td style="padding: 8px;">%d</td></tr>
                <tr style="background-color: #f9f9f9;">
                    <td style="padding: 8px;"><strong>Total</strong></td>
                    <td style="padding: 8px;"><strong>S/ %.2f</strong></td>
                </tr>
            </table>

            <p style="margin-top: 20px;">Gracias por confiar en nuestro hospedaje. ¡Te esperamos!</p>
            <p style="font-size: 12px; color: gray;">Este es un mensaje automático. Por favor, no respondas a este correo.</p>
        </div>
        """.formatted(
                d.getCliente(),
                d.getCodigoReserva(),
                d.getNumHabitacion(),
                d.getTipoHabitacion(),
                d.getPiso(),
                d.getFechaEntrada(),
                d.getFechaSalida(),
                d.getPrecioPorDia(),
                d.getCantidadDias(),
                d.getTotal()
        );
    }
}
