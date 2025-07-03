package sistema_reservas.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Habitaciones {
    @Id
    private Integer idhabitacion;

    private Integer numhabitacion;

    private Integer tipohabid;

    private Integer pisoid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipohabid", insertable = false, updatable = false)
    private TipoHabitacion tipoHabitacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pisoid", insertable = false, updatable = false)
    private Pisos pisos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estadohabid", insertable = false, updatable = false)
    private EstadoHabitacion estadoHabitacion;

    private Integer estadohabid;
}
