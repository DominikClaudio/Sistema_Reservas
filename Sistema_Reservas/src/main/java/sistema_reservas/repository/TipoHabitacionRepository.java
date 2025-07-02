package sistema_reservas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sistema_reservas.model.TipoHabitacion;

public interface TipoHabitacionRepository extends JpaRepository<TipoHabitacion, Integer> {
}
