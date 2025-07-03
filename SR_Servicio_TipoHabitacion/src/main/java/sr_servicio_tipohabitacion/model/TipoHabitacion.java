package sr_servicio_tipohabitacion.model;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@XmlRootElement
@Data
@Table(name = "TipoHabitacion")
@Entity
public class TipoHabitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idtipohab;
    private String nombre;
    private String imagenurl;
    private double precioxdia;
    private String descripcionhab;
    private Integer capacidad;
}
