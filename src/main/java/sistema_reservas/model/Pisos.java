package sistema_reservas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Pisos {

    @Id
    private Integer idpiso;
    private Integer numero;
    private String nombre;
}
