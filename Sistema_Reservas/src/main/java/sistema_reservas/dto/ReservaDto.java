package sistema_reservas.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ReservaDto {
    private int idreserva;
    private int numhabitacion;
    private String tipoHabitacion;
    private LocalDate fechaentrada;
    private int cantidaddias;
    private String estadoReserva;
    private int idHabitacion;
}

