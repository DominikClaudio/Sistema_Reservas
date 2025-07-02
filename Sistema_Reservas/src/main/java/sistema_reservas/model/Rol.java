package sistema_reservas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "Roles")
@Data
public class Rol {
	
	@Id
	private Integer idrol;
	
	private String nombre;

}
