package sistema_reservas.model;

import jakarta.persistence.Column;
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
    @Column(name = "precioxdia")
    private Double precioxDia;
    @Column(name = "descripcionhab")
    private String descripcionHab;
    private Integer capacidad;

}

