package sistema_reservas.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HabitacionSeleccionadaDto {
    private int idhabitacion;
    private String tipoHabitacion;
    private int numHabitacion;
    private String piso;
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private double precioxDia;
    private int cantidadDias;
    private double total;
}
