package sistema_reservas.dto;

import lombok.Data;

@Data
public class ReporteHabFrecuenteDto {

    private String numHabitacion;
    private String piso;
    private String tipoHabitacion;
    private int vecesReservada;
}
