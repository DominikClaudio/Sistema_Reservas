package sr_servicio_habitaciones.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Pisos {
    @Id
    private Integer idpiso;
    private String nombre;
}
