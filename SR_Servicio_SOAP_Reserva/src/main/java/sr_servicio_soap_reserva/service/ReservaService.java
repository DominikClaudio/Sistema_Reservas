package sr_servicio_soap_reserva.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sr_servicio_soap_reserva.dao.ReservaRepository;
import sr_servicio_soap_reserva.dto.DetalleReservaDto;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    public DetalleReservaDto obtenerReservaPorCodigo(String codigo) {
        return reservaRepository.buscarPorCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
    }

}
