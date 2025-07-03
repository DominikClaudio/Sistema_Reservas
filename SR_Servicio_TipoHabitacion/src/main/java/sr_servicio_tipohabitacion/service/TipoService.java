package sr_servicio_tipohabitacion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import sr_servicio_tipohabitacion.model.TipoHabitacion;
import sr_servicio_tipohabitacion.repository.TipoRepository;

import java.util.List;

@Service
public class TipoService {

    @Autowired
    private TipoRepository tipoRepository;

    public List<TipoHabitacion> listarTipos(){
        return tipoRepository.findAll();
    }

    public TipoHabitacion buscarPorIdTipo(int id){
        return tipoRepository.findById(id).orElse(null);
    }

    public TipoHabitacion registrarTipo (TipoHabitacion tipo){
        return tipoRepository.save(tipo);
    }

    public void eliminarTipo (int id){
        try {
            tipoRepository.deleteById(id);
        } catch (DataIntegrityViolationException e){
            throw new IllegalStateException("Nose puede eliminar el tipo de habitación porque esta asosciado a unas habitaciones.");
        }

    }
}
