package sr_servicio_pisos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@XmlRootElement
@Data
@Entity
public class Pisos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idpiso;
    private Integer numero;
    private String nombre;
}
