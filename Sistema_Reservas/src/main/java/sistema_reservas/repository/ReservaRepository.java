package sistema_reservas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sistema_reservas.model.Reserva;
import sistema_reservas.model.Usuario;

//2) Crea un repositorio JPA para Reserva
//src/main/java/sistema_reservas/repository/ReservaRepository.java
public interface ReservaRepository extends JpaRepository<Reserva,Integer> {

	List<Reserva> findByUsuario(Usuario u); 
}
