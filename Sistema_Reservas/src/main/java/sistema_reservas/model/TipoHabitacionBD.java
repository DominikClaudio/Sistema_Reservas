package sistema_reservas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
public class TipoHabitacionBD {

    private Integer idtipohab;
    private String nombre;
    private String imagenurl;
    private Double precioxdia;
    private String descripcionhab;
    private Integer capacidad;

}

