package sistema_reservas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sistema_reservas.model.EstadoReserva;

public interface EstadoReservaRepository extends JpaRepository<EstadoReserva, Integer> {

}
