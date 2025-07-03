package sistema_reservas.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import sistema_reservas.model.Pisos;

import java.util.List;

@FeignClient(name = "servicio-pisos", url = "http://localhost:8083/api")
public interface PisoClient {

    @GetMapping("/pisos")
    List<Pisos> listarPisos();

    @GetMapping("/pisos/{id}")
    Pisos obtenerPorIdPiso(@PathVariable("id") int id);

    @PostMapping("/pisos")
    Pisos guardarPiso(@RequestBody Pisos piso);

    @PutMapping("/pisos/{id}")
    Pisos actualizarPiso(@PathVariable("id") int id, @RequestBody Pisos piso);

    @DeleteMapping("/pisos/{id}")
    void eliminarPiso(@PathVariable("id") int id);


}
