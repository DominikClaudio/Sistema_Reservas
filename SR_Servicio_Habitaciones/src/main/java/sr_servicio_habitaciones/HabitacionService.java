package sr_servicio_habitaciones;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sr_servicio_habitaciones.model.Habitaciones;
import sr_servicio_habitaciones.repository.HabitacionRepository;

import java.util.List;

@Service
public class HabitacionService {

    @Autowired
    private HabitacionRepository habitacionRepository;

    public List<Habitaciones> listarHabitaciones(){
        return habitacionRepository.findAll();
    }

    public Habitaciones buscarPorIdHabitacion(int id){
        return habitacionRepository.findById(id).orElse(null);
    }

    public Habitaciones registrarHabitacion (Habitaciones hab){
        return habitacionRepository.save(hab);
    }

    public void eliminarHabitacion(int id){
        habitacionRepository.deleteById(id);
    }
}
