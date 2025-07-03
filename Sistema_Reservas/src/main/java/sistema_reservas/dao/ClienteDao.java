package sistema_reservas.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import sistema_reservas.dto.DetalleCorreoDto;
import sistema_reservas.dto.ReservaListadoDto;

import java.time.LocalDate;

@Repository
public class ClienteDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public DetalleCorreoDto obtenerDetalleCorreo(Integer reservaId){
        String sql = "CALL SP_DetalleReserva(?)";

        return jdbcTemplate.queryForObject(sql,
                new Object[]{reservaId},
                (rs, rowNum) -> {
                    DetalleCorreoDto dto = new DetalleCorreoDto();
                    dto.setCorreoDestino(rs.getString("CorreoDestino"));
                    dto.setCodigoReserva(rs.getString("CodigoReserva"));
                    dto.setCliente(rs.getString("Cliente"));
                    dto.setNumHabitacion(rs.getInt("NumHabitacion"));
                    dto.setTipoHabitacion(rs.getString("TipoHabitacion"));
                    dto.setPiso(rs.getString("Piso"));
                    dto.setFechaEntrada(rs.getDate("FechaEntrada").toLocalDate());
                    dto.setFechaSalida(rs.getDate("FechaSalida").toLocalDate());
                    dto.setPrecioPorDia(rs.getDouble("PrecioPorDia"));
                    dto.setCantidadDias(rs.getInt("CantidadDias"));
                    dto.setTotal(rs.getDouble("Total"));
                    return dto;
                });
    }

    public ReservaListadoDto obtenerDetalleReserva(int reservaId) {
        String sql = """
        SELECT
            R.codigoreserva AS codigoReserva,
            CONCAT(U.nombre, ' ', U.apellido) AS cliente,
            H.numhabitacion AS numHabitacion,
            TH.nombre AS tipoHabitacion,
            P.nombre AS piso,
            R.fechaentrada AS fechaEntrada,
            R.fechasalida AS fechaSalida,
            DATEDIFF(R.fechasalida, R.fechaentrada) AS cantidadDias,
            TH.precioxdia AS precioxDia,
            DATEDIFF(R.fechasalida, R.fechaentrada) * TH.precioxdia AS total,
            ER.nombre AS estadoReserva
        FROM Reservas R
        INNER JOIN DetalleReserva DR ON R.idreserva = DR.reservaid
        INNER JOIN Habitaciones H ON DR.habitacionid = H.idhabitacion
        INNER JOIN TipoHabitacion TH ON H.tipohabid = TH.idtipohab
        INNER JOIN Pisos P ON H.pisoid = P.idpiso
        INNER JOIN EstadoReserva ER ON R.estadoid = ER.idestadores
        INNER JOIN Usuarios U ON R.usuarioid = U.idusuario
        WHERE R.idreserva = ?
        LIMIT 1
    """;


        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(ReservaListadoDto.class), reservaId);
    }

}
