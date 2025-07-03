package sr_servicio_soap_reserva.dto;

import java.time.LocalDate;

public record DetalleReservaDto(
        int idReserva,
        String codigoReserva,
        String cliente,
        String numHabitacion,
        String tipoHabitacion,
        String piso,
        LocalDate fechaEntrada,
        LocalDate fechaSalida,
        double precioxDia,
        int cantidadDias,
        double total,
        String estadoReserva
) {
}
