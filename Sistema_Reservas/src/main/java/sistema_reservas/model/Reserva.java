package sistema_reservas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reservas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idreserva")
    private Integer idreserva;

    @Column(name = "codigoreserva", nullable = false, length = 50)
    private String codigoreserva;

    @ManyToOne
    @JoinColumn(name = "usuarioid")
    private Usuario usuario;

    @Column(name = "fechaentrada")
    private LocalDate fechaentrada;

    @Column(name = "fechasalida")
    private LocalDate fechasalida;

    @Column(name = "estadoid")
    private Integer estadoid;

    @Column(name = "horaentrada")
    private LocalTime horaentrada;

    @Column(name = "horasalida")
    private LocalTime horasalida;
    
    

    @Transient
    public String getCodigo() {
        return this.codigoreserva;
    }
}
