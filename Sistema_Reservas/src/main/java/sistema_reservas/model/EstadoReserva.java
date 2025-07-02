package sistema_reservas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class EstadoReserva {
    @Id
    private Integer idestadores;

    private String nombre;
}

