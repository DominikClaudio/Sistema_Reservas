package sr_servicio_soap_reserva.soap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import sr_servicio_soap_reserva.dto.DetalleReservaDto;
import sr_servicio_soap_reserva.reserva.ConsultarReservaRequest;
import sr_servicio_soap_reserva.reserva.ConsultarReservaResponse;
import sr_servicio_soap_reserva.service.ReservaService;

import java.math.BigDecimal;

@Endpoint
public class ReservaEndpoint {

    private static final String NAMESPACE_URI = "http://hospedaje.com/soap/reserva";

    @Autowired
    private ReservaService reservaService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ConsultarReservaRequest")
    @ResponsePayload
    public ConsultarReservaResponse buscarReserva(@RequestPayload ConsultarReservaRequest request) {
        DetalleReservaDto dto = reservaService.obtenerReservaPorCodigo(request.getCodigoReserva());

        ConsultarReservaResponse response = new ConsultarReservaResponse();
        response.setCodReserva(dto.codigoReserva());
        response.setCliente(dto.cliente());
        response.setHabitacion(dto.numHabitacion());
        response.setTipo(dto.tipoHabitacion());
        response.setPiso(dto.piso());
        response.setFechaEntrada(dto.fechaEntrada().toString());
        response.setFechaSalida(dto.fechaSalida().toString());
        response.setPrecioPorDia(BigDecimal.valueOf(dto.precioxDia()));
        response.setDias(dto.cantidadDias());
        response.setTotal(BigDecimal.valueOf(dto.total()));

        return response;
    }


}
