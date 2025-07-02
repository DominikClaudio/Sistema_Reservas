package sistema_reservas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sistema_reservas.model.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {

	sistema_reservas.model.Rol findByNombre(String nombre);
}

