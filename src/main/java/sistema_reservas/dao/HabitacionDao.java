package sistema_reservas.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import sistema_reservas.dto.HabitacionDto;
import sistema_reservas.dto.HabitacionListadoDto;
import sistema_reservas.dto.PisoDto;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public class HabitacionDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<HabitacionListadoDto> listarHabitacionesFiltro(
            Integer tipoHabId,
            Integer estadoHabId,
            Integer estadoReservaId,
            Date fechaInicio,
            Date fechaFin
    ){
        return jdbcTemplate.query(
                "CALL SP_ListarHabTipoEstado(?, ?, ?, ?, ?)",
                new BeanPropertyRowMapper<>(HabitacionListadoDto.class),
                tipoHabId,
                estadoHabId,
                estadoReservaId,
                fechaInicio,
                fechaFin);
    }

    /*CLIENTE*/
    public List<PisoDto> listarPisosPorTipo(int tipohabId) {
        return jdbcTemplate.query(
                "CALL SP_ListarPisosPorTipo(?)",
                new Object[]{tipohabId},
                (rs, rowNum) -> {
                    PisoDto piso = new PisoDto();
                    piso.setIdpiso(rs.getInt("idpiso"));
                    piso.setNombre(rs.getString("nombre"));
                    return piso;
                }
        );
    }

    public List<HabitacionDto> listarHabitacionesDisponibles(
            LocalDate fechaEntrada,
            LocalDate fechaSalida,
            Integer tipohabId,
            Integer pisoId,
            Integer cantidadPersonas
    ) {
        return jdbcTemplate.query(
                "CALL SP_ListarHabitacionesDisponibles(?, ?, ?, ?, ?)",
                new BeanPropertyRowMapper<>(HabitacionDto.class),
                fechaEntrada,
                fechaSalida,
                tipohabId,
                pisoId,
                cantidadPersonas);
    }


}
