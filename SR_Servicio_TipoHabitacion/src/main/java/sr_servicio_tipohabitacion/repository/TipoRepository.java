package sr_servicio_tipohabitacion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sr_servicio_tipohabitacion.model.TipoHabitacion;

@Repository
public interface TipoRepository extends JpaRepository<TipoHabitacion, Integer> {
}
