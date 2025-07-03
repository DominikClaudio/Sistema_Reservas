package sr_servicio_habitaciones.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sr_servicio_habitaciones.model.Habitaciones;

import java.util.List;

@Repository
public interface HabitacionRepository extends JpaRepository<Habitaciones, Integer> {

    @EntityGraph(attributePaths = {"tipoHabitacion", "pisos", "estadoHabitacion"})
    List<Habitaciones> findAll();
}
