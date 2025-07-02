package sistema_reservas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sistema_reservas.dto.TipoHabitacionDto;

public interface TipoHabitacionDtoRepository extends JpaRepository<TipoHabitacionDto, Integer> {
}
