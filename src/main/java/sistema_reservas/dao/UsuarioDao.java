package sistema_reservas.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import sistema_reservas.dto.UsuarioListadoDto;
import sistema_reservas.model.Usuario;

import java.util.List;

@Repository
public class UsuarioDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<UsuarioListadoDto> listarUsuariosPorRol(Integer rolid) {
        return jdbcTemplate.query(
                "CALL SP_ListarUsuariosPorRol(?)",
                new BeanPropertyRowMapper<>(UsuarioListadoDto.class),
                rolid
        );
    }

    public Usuario obtenerPerfilId(Integer usuarioId) {
        return jdbcTemplate.queryForObject(
                "CALL SP_Perfil(?)",
                new BeanPropertyRowMapper<>(Usuario.class),
                usuarioId);
    }

    public Usuario obtenerPerfilCorreo(String correo) {
        return jdbcTemplate.queryForObject(
                "SELECT idusuario, nombre, apellido, dni, correo, password FROM Usuarios WHERE correo = ?",
                new BeanPropertyRowMapper<>(Usuario.class),
                correo);
    }

    public void actualizarPerfil(
            Integer id, String nombre, String apellido, Integer dni, String correo, String contrasena ) {
        jdbcTemplate.update(
                "CALL SP_ActualizarPerfil(?, ?, ?, ?, ?, ?)",
                id, nombre, apellido, dni, correo, contrasena);
    }

}
