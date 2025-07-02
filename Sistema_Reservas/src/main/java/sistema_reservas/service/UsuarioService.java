package sistema_reservas.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import sistema_reservas.dao.UsuarioDao;
import sistema_reservas.dto.UsuarioListadoDto;
import sistema_reservas.model.Usuario;
import sistema_reservas.repository.UsuarioRepository;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UsuarioDao usuarioDao;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        System.out.println("Buscando usuario con correo: " + correo);
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> {
                    System.out.println("Usuario no encontrado: " + correo);
                    return new UsernameNotFoundException("Usuario no encontrado: " + correo);
                });
        System.out.println("Usuario encontrado: " + usuario.getCorreo() + ", Rol: " + usuario.getRol());
        return User.builder()
                .username(usuario.getCorreo())
                .password(usuario.getPassword())
                .roles(usuario.getRol().getNombre())
                .build();
    }

    public List<UsuarioListadoDto> obtenerUsuarios(Integer rolid){
        return usuarioDao.listarUsuariosPorRol(rolid);
    }

    public Usuario obtenerPerfil(Integer usuarioId) {
        return usuarioDao.obtenerPerfilId(usuarioId);
    }

    public Usuario obtenerPerfilPorCorreo(String correo) {
        return usuarioDao.obtenerPerfilCorreo(correo);
    }

    public void actualizarPerfil(Usuario usuario) {
        String hash = passwordEncoder.encode(usuario.getPassword());

        usuarioDao.actualizarPerfil(
                usuario.getIdusuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getDni(),
                usuario.getCorreo(),
                hash);
    }

}
