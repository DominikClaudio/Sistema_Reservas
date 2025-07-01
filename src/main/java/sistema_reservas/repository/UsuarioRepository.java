package sistema_reservas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import sistema_reservas.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{
	
	Optional<Usuario> findByCorreo(String correo);
	//Optional<Usuario> findById(String correo);
	

}
