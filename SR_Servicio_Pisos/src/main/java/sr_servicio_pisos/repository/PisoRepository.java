package sr_servicio_pisos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sr_servicio_pisos.model.Pisos;

@Repository
public interface PisoRepository extends JpaRepository<Pisos, Integer> {
}
