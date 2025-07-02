package sistema_reservas.dto;

import java.util.Date;

import lombok.Data;

@Data
public class HabitacionListadoDto {
	
	private Integer idhabitacion;
    private Integer numHabitacion;
    private Double precioxDia;
    private String tipoHabitacion;
    private String piso;
    private Integer estadoHabId;
    private String estadoHabitacion;
    private Integer estadoReservaId;
    private String estadoReserva;
    private String cliente;
    private Date fechaEntrada;
    private Date fechaSalida;
}
