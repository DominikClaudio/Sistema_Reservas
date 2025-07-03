package sr_servicio_pisos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import sr_servicio_pisos.model.Pisos;
import sr_servicio_pisos.repository.PisoRepository;

import java.util.List;

@Service
public class PisoService {

    @Autowired
    private PisoRepository pisoRepository;

    public List<Pisos> listarPisos(){
        return pisoRepository.findAll();
    }

    public Pisos buscarPorIdPiso(int id){
        return pisoRepository.findById(id).orElse(null);
    }

    public Pisos registrarPiso(Pisos piso){
        return pisoRepository.save(piso);
    }

    public void eliminarPiso(int id){
        try {
            pisoRepository.deleteById(id);
        }catch (DataIntegrityViolationException e){
            throw new IllegalStateException("No se puede eliminar el piso porque tiene una habitacion asociada.");
        }
    }
}
