package sistema_reservas.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import sistema_reservas.model.Habitaciones;

import java.util.List;

public interface HabitacionRepository extends JpaRepository<Habitaciones, Integer> {

}
