package sistema_reservas.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import sistema_reservas.dto.*;

import java.sql.Types;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AdminDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void actualizarEstadoHabitacion(@RequestParam int idHabitacion, @RequestParam int estadoHabId ){
        jdbcTemplate.update("CALL SP_CambiarEstadoHabitacion(?, ?)",
        idHabitacion,
        estadoHabId);
    }

    public void actualizarRolUsuario(int usuarioId, int nuevoRolId) {
        jdbcTemplate.update("CALL SP_ActualizarRolUsuario(?, ?)", usuarioId, nuevoRolId);
    }

    public UsuarioListadoDto obtenerUsuarioPorId(int id) {
        String sql = "SELECT * FROM Usuarios WHERE idusuario = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(UsuarioListadoDto.class), id);
    }

    public List<RolDto> listarRoles() {
        String sql = "SELECT idrol, nombre FROM Roles";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            RolDto dto = new RolDto();
            dto.setId(rs.getInt("idrol"));
            dto.setNombre(rs.getString("nombre"));
            return dto;
        });
    }

    public List<ReporteReservasDto> obtenerReporteReservasConfirmadas(LocalDate fechaEntrada, LocalDate fechaSalida){
        return jdbcTemplate.query(
                "CALL SP_ReporteReservasConfirmadas(?, ?)",
                new BeanPropertyRowMapper<>(ReporteReservasDto.class),
                fechaEntrada,
                fechaSalida);
    }

    public int obtenerTotalReservasConfirmadas(LocalDate fechaEntrada, LocalDate fechaSalida){
        return jdbcTemplate.queryForObject(
                "CALL SP_TotalReservasConfirmadas(?, ?)",
                Integer.class,
                fechaEntrada,
                fechaSalida);
    }

    public List<ReporteClientesDto> obtenerClientesFrecuentes(LocalDate fechaInicio, LocalDate fechaFin) {
        return jdbcTemplate.query(
                "CALL SP_ReporteClientesFrecuentes(?, ?)",
                new Object[]{fechaInicio, fechaFin},
                (rs, rowNum) -> {
                    ReporteClientesDto dto = new ReporteClientesDto();
                    dto.setNombre(rs.getString("Nombre"));
                    dto.setApellido(rs.getString("Apellido"));
                    dto.setDni(rs.getString("DNI"));
                    dto.setTotalReservas(rs.getInt("TotalReservas"));
                    return dto;
                }
        );
    }

    public List<ReporteHabFrecuenteDto> obtenerHabitacionesFrecuentes(LocalDate fechaInicio, LocalDate fechaFin) {
        return jdbcTemplate.query(
                "CALL SP_ReporteHabitacionesFrecuentes(?, ?)",
                new Object[]{fechaInicio, fechaFin},
                (rs, rowNum) -> {
                    ReporteHabFrecuenteDto dto = new ReporteHabFrecuenteDto();
                    dto.setNumHabitacion(rs.getString("NumHabitacion"));
                    dto.setPiso(rs.getString("Piso"));
                    dto.setTipoHabitacion(rs.getString("TipoHabitacion"));
                    dto.setVecesReservada(rs.getInt("VecesReservada"));
                    return dto;
                }
        );
    }

    public List<ReporteOcupabilidadPisoDto> obtenerOcupabilidadPorPisos(LocalDate fechaInicio, LocalDate fechaFin) {
        return jdbcTemplate.query(
                "CALL SP_ReporteOcupabilidadPisos(?, ?)",
                new Object[]{fechaInicio, fechaFin},
                (rs, rowNum) -> {
                    ReporteOcupabilidadPisoDto dto = new ReporteOcupabilidadPisoDto();
                    dto.setPiso(rs.getString("Piso"));
                    dto.setTotalReservas(rs.getInt("TotalReservas"));
                    return dto;
                }
        );
    }

    public void confirmarReserva(Integer reservaId){
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("SP_ConfirmarReserva")
                .declareParameters(new SqlParameter("p_reservaId", Types.INTEGER));

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("p_reservaId", reservaId);
        jdbcCall.execute(parametros);
    }

    public void finalizarReserva(Integer reservaId){
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("SP_FinalizarReserva")
                .declareParameters(new SqlParameter("p_reservaId", Types.INTEGER));

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("p_reservaId", reservaId);
        jdbcCall.execute(parametros);
    }
}
