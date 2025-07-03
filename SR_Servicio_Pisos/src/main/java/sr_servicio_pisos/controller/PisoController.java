package sr_servicio_pisos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sr_servicio_pisos.model.Pisos;
import sr_servicio_pisos.service.PisoService;

import java.util.List;

@RestController
@RequestMapping("/api/pisos")
public class PisoController {

    @Autowired
    private PisoService pisoService;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<Pisos> listar(){
        return pisoService.listarPisos();
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Pisos> obtener (@PathVariable int id){
        Pisos piso = pisoService.buscarPorIdPiso(id);
        return piso != null ? ResponseEntity.ok(piso) : ResponseEntity.notFound().build();
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Pisos> registrar (@RequestBody Pisos piso){
        return ResponseEntity.ok(pisoService.registrarPiso(piso));
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Pisos> actualizar (@PathVariable int id, @RequestBody Pisos piso){
        Pisos existente = pisoService.buscarPorIdPiso(id);

        if (existente == null){
            return ResponseEntity.notFound().build();
        }

        existente.setNumero(piso.getNumero());
        existente.setNombre(piso.getNombre());

        return ResponseEntity.ok(pisoService.registrarPiso(existente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar (@PathVariable int id){
        try {
            pisoService.eliminarPiso(id);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
