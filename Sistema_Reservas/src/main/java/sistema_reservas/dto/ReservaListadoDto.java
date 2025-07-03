package sistema_reservas.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservaListadoDto {

    private int idReserva;
    private String codigoReserva;
    private String cliente;
    private int numHabitacion;
    private String tipoHabitacion;
    private String piso;
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private double precioxDia;
    private int cantidadDias;
    private double total;
    private String estadoReserva;
}
