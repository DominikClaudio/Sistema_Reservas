package sistema_reservas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class TipoHabitacion {
	@Id
    private Integer idtipohab;
    private String nombre;
    private String imagenUrl;
    private Double precioxDia;
    private String descripcionHab;
}

