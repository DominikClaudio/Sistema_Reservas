package sr_servicio_correos.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class DetalleCorreoDto{
    private String correoDestino;
    private String codigoReserva;
    private String cliente;

    private Integer numHabitacion;
    private String tipoHabitacion;
    private String piso;

    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;

    private double precioPorDia;
    private int cantidadDias;
    private double total;
}
