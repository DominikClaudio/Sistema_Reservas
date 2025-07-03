package sistema_reservas.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import sistema_reservas.model.TipoHabitacionBD;

import java.util.List;

@FeignClient(name = "service-tipohabitacion", url = "http://localhost:8084/api")
public interface TipoHabitacionClient {

    @GetMapping("/tipohabitacion")
    List<TipoHabitacionBD> listarTipos();

    @GetMapping("/tipohabitacion/{id}")
    TipoHabitacionBD obtenerPorIdTipo(@PathVariable("id") int id);

    @PostMapping("/tipohabitacion")
    TipoHabitacionBD guardarTipo(@RequestBody TipoHabitacionBD tipo);

    @PutMapping("/tipohabitacion/{id}")
    TipoHabitacionBD actualizarTipo(@PathVariable("id") int id, @RequestBody TipoHabitacionBD tipo);

    @DeleteMapping("/tipohabitacion/{id}")
    void eliminarTipo(@PathVariable("id") int id);
}