package sistema_reservas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class EstadoHabitacion {
    @Id
    private Integer idestadohab;

    private String nombre;
}

