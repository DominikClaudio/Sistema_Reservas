package sistema_reservas.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "TipoHabitacion")
@Data
public class TipoHabitacionDto {

    @Id
    private Integer idtipohab;

    private String nombre;
}
