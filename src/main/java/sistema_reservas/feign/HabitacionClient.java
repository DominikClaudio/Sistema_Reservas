package sistema_reservas.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import sistema_reservas.model.Habitaciones;

import java.util.List;

@FeignClient(name = "servicio-habitaciones", url = "http://localhost:8082/api")
public interface HabitacionClient {

    @GetMapping("/habitaciones")
    List<Habitaciones> listarHabitaciones();

    @GetMapping("/habitaciones/{id}")
    Habitaciones obtenerPorIdHabitacion(@PathVariable("id") int id);

    @PostMapping("/habitaciones")
    Habitaciones guardarHabitacion(@RequestBody Habitaciones hab);

    @PutMapping("/habitaciones/{id}")
    Habitaciones actualizarHabitacion(@PathVariable("id") int id, @RequestBody Habitaciones hab);

    @DeleteMapping("/habitaciones/{id}")
    void eliminarHabitacion(@PathVariable("id") int id);
}
