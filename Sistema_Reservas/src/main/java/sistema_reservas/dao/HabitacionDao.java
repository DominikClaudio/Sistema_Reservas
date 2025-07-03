package sistema_reservas.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import sistema_reservas.dto.*;

import java.sql.CallableStatement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public List<HabitacionSeleccionadaDto> obtenerDetalleSeleccion(LocalDate fechaEntrada, LocalDate fechaSalida, String habIdsCsv){
        return  jdbcTemplate.query(
                "CALL SP_DetalleHabitacionesSeleccionadas(?, ?, ?)",
                new BeanPropertyRowMapper<>(HabitacionSeleccionadaDto.class),
                fechaEntrada,
                fechaSalida,
                habIdsCsv);
    }

    public List<Integer> registrarReservaConDetalles(int usuarioId, LocalDate fechaEntrada, LocalDate fechaSalida, int estadoId, String listaHabitaciones) {
        // Ejecutar el procedimiento
        List<Integer> idsReserva = jdbcTemplate.query(
                "CALL SP_RegistrarReservaConDetalles(?, ?, ?, ?, ?)",
                new Object[]{usuarioId, fechaEntrada, fechaSalida, estadoId, listaHabitaciones},
                (rs, rowNum) -> rs.getInt("idreserva")
        );

        return idsReserva;
    }


//    public Integer registrarReservaConDetalles(int usuarioId, LocalDate fechaEntrada, LocalDate fechaSalida, int estadoId, String listaHabitaciones) {
//
//        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
//                .withProcedureName("SP_RegistrarReservaConDetalles")
//                .declareParameters(
//                        new SqlParameter("p_usuarioId", Types.INTEGER),
//                        new SqlParameter("p_fechaEntrada", Types.DATE),
//                        new SqlParameter("p_fechaSalida", Types.DATE),
//                        new SqlParameter("p_estadoId", Types.INTEGER),
//                        new SqlParameter("p_listaHabitaciones", Types.VARCHAR),
//                        new SqlOutParameter("p_idReserva", Types.INTEGER)
//                );
//
//        Map<String, Object> result = jdbcCall.execute(new MapSqlParameterSource()
//                .addValue("p_usuarioId", usuarioId)
//                .addValue("p_fechaEntrada", fechaEntrada)
//                .addValue("p_fechaSalida", fechaSalida)
//                .addValue("p_estadoId", estadoId)
//                .addValue("p_listaHabitaciones", listaHabitaciones));
//        System.out.println("Resultado del SP: " + result);
//
//        return (Integer) result.get("p_idReserva");
//
//    }

    public List<ReservaDto> listarMisReservas(int usuarioId){
        return jdbcTemplate.query(
                "CALL SP_ListarMisReservas(?)",
                new BeanPropertyRowMapper<>(ReservaDto.class),
                usuarioId);
    }

    public void cancelarReserva(int reservaId) {
        jdbcTemplate.update("CALL SP_CancelarReserva(?)", reservaId);
    }

    public List<ReservaListadoDto> buscarPorCodigoReserva(String codigoReserva) {
        return jdbcTemplate.query("CALL SP_BuscarReservaPorCodigo(?)",
                new Object[]{codigoReserva},
                (rs, rowNum) -> {
                    ReservaListadoDto dto = new ReservaListadoDto();
                    dto.setIdReserva(rs.getInt("idReserva"));
                    dto.setCodigoReserva(rs.getString("codigoReserva"));
                    dto.setCliente(rs.getString("cliente"));
                    dto.setNumHabitacion(rs.getInt("numHabitacion"));
                    dto.setTipoHabitacion(rs.getString("tipoHabitacion"));
                    dto.setPiso(rs.getString("piso"));
                    dto.setFechaEntrada(rs.getDate("fechaEntrada").toLocalDate());
                    dto.setFechaSalida(rs.getDate("fechaSalida").toLocalDate());
                    dto.setPrecioxDia(rs.getDouble("precioxDia"));
                    dto.setCantidadDias(rs.getInt("cantidadDias"));
                    dto.setTotal(rs.getDouble("total"));
                    dto.setEstadoReserva(rs.getString("estadoReserva"));
                    return dto;
                }
        );
    }



}
