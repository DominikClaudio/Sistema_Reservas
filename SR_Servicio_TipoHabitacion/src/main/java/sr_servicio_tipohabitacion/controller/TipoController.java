package sr_servicio_tipohabitacion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sr_servicio_tipohabitacion.model.TipoHabitacion;
import sr_servicio_tipohabitacion.service.TipoService;

import java.util.List;

@RestController
@RequestMapping("/api/tipohabitacion")
public class TipoController {

    @Autowired
    private TipoService tipoService;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<TipoHabitacion> listar(){
        return tipoService.listarTipos();
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<TipoHabitacion> obtener(@PathVariable int id){
        TipoHabitacion tipo = tipoService.buscarPorIdTipo(id);
        return tipo != null ? ResponseEntity.ok(tipo) : ResponseEntity.notFound().build();
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<TipoHabitacion> registrar(@RequestBody TipoHabitacion tipo){
        return ResponseEntity.ok(tipoService.registrarTipo(tipo));
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<TipoHabitacion> actualizar(@PathVariable int id, @RequestBody TipoHabitacion tipo){
        TipoHabitacion existente = tipoService.buscarPorIdTipo(id);

        if (existente == null){
            return ResponseEntity.notFound().build();
        }

        existente.setNombre(tipo.getNombre());
        existente.setImagenurl(tipo.getImagenurl());
        existente.setPrecioxdia(tipo.getPrecioxdia());
        existente.setDescripcionhab(tipo.getDescripcionhab());
        existente.setCapacidad(tipo.getCapacidad());

        return ResponseEntity.ok(tipoService.registrarTipo(existente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable int id){
        try {
            tipoService.eliminarTipo(id);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
