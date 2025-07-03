package sistema_reservas.dto;

import lombok.Data;

@Data
public class ReporteClientesDto {

    private String nombre;
    private String apellido;
    private String dni;
    private int totalReservas;
}
