package sistema_reservas.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sistema_reservas.dao.HabitacionDao;
import sistema_reservas.dto.HabitacionListadoDto;

@Service
public class HabitacionService {

	@Autowired
	private HabitacionDao habitacionDao;
	
	public List<HabitacionListadoDto> obtenerHabitacionesAdmin(){
		return habitacionDao.listarHabitacionesFiltro(null, null, null, null, null);
	}
	
	public List<HabitacionListadoDto> obtenerHabitacionesFiltro(
			Integer tipoHabId, Integer estadoHabId, Integer estadoReservaId,
			Date fechaInicio, Date fechaFin){
		return habitacionDao.listarHabitacionesFiltro(
				tipoHabId, estadoHabId, estadoReservaId, fechaInicio, fechaFin);
	}
}
