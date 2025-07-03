package sr_servicio_soap_reserva.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import sr_servicio_soap_reserva.dto.DetalleReservaDto;

import java.util.Optional;

@Repository
public class ReservaRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Optional<DetalleReservaDto> buscarPorCodigo(String codigo) {
        String sql = "CALL SP_BuscarReservaPorCodigo(?)";
        return jdbcTemplate.query(sql, new Object[]{codigo}, rs -> {
            if (rs.next()) {
                return Optional.of(new DetalleReservaDto(
                        rs.getInt("idReserva"),
                        rs.getString("codigoReserva"),
                        rs.getString("cliente"),
                        rs.getString("numHabitacion"),
                        rs.getString("tipoHabitacion"),
                        rs.getString("piso"),
                        rs.getDate("fechaEntrada").toLocalDate(),
                        rs.getDate("fechaSalida").toLocalDate(),
                        rs.getDouble("precioxDia"),
                        rs.getInt("cantidadDias"),
                        rs.getDouble("total"),
                        rs.getString("estadoReserva")
                ));
            } else {
                return Optional.empty();
            }
        });
    }
}
