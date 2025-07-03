package sr_servicio_habitaciones.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class TipoHabitacion {
    @Id
    private Integer idtipohab;
    private String nombre;
}
