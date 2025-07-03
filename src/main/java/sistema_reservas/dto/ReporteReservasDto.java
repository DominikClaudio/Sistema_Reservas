package sistema_reservas.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ReporteReservasDto {
    private String codigoReserva;
    private int numHabitacion;
    private String piso;
    private String nombreCliente;
    private String apellidoCliente;
    private int dni;
    private LocalDate fechaEntrada;
    private LocalTime horaEntrada;
    private LocalDate fechaSalida;
    private LocalTime horaSalida;
    private String tipoHabitacion;
    private double precioxDia;
    private double total;
    private String estado;
}