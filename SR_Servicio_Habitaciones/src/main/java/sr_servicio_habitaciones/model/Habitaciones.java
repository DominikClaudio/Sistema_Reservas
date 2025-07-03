package sr_servicio_habitaciones.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@XmlRootElement
@Data
@Entity
public class Habitaciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idhabitacion;
    private String numhabitacion;
    private Integer tipohabid;
    private Integer pisoid;
    private Integer estadohabid;
    
    @ManyToOne
    @JoinColumn(name = "tipohabid", referencedColumnName = "idtipohab", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private TipoHabitacion tipoHabitacion;

    @ManyToOne
    @JoinColumn(name = "pisoid", referencedColumnName = "idpiso", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Pisos pisos;

    @ManyToOne
    @JoinColumn(name = "estadohabid", referencedColumnName = "idestadohab", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private EstadoHabitacion estadoHabitacion;
}
