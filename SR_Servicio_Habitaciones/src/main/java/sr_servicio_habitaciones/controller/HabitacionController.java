package sr_servicio_habitaciones.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sr_servicio_habitaciones.HabitacionService;
import sr_servicio_habitaciones.model.Habitaciones;
import sr_servicio_habitaciones.repository.HabitacionRepository;

import java.util.List;

@RestController
@RequestMapping("/api/habitaciones")
public class HabitacionController {

    @Autowired
    private HabitacionService habitacionService;


    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<Habitaciones> listar(){
        return habitacionService.listarHabitaciones();
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Habitaciones> obtener(@PathVariable int id){
        Habitaciones hab = habitacionService.buscarPorIdHabitacion(id);
        return hab != null ? ResponseEntity.ok(hab) : ResponseEntity.notFound().build();
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Habitaciones> registrar(@RequestBody Habitaciones hab){
        return ResponseEntity.ok(habitacionService.registrarHabitacion(hab));
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Habitaciones> actualizar(@PathVariable int id, @RequestBody Habitaciones hab){
        Habitaciones existente = habitacionService.buscarPorIdHabitacion(id);

        if (existente == null){
            return ResponseEntity.notFound().build();
        }

        existente.setNumhabitacion(hab.getNumhabitacion());
        existente.setTipohabid(hab.getTipohabid());
        existente.setPisoid(hab.getPisoid());
        existente.setEstadohabid(hab.getEstadohabid());

        return ResponseEntity.ok(habitacionService.registrarHabitacion(existente));
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable int id){
        habitacionService.eliminarHabitacion(id);
    }
}
