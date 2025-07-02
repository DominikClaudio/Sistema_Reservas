package sistema_reservas.login;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sistema_reservas.model.Usuario;
import sistema_reservas.repository.UsuarioRepository;

@Component
public class ManejadorLogin implements AuthenticationSuccessHandler {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        System.out.println("LOGIN ÉXITOSO para: " + authentication.getName());

        String correo = authentication.getName(); // <- corregido aquí también
        Usuario usuario = usuarioRepository.findByCorreo(correo).orElse(null);

        if (usuario != null) {
            request.getSession().setAttribute("usuarioId", usuario.getIdusuario());
            request.getSession().setAttribute("usuarioNombre", usuario.getNombre());
        }

        response.sendRedirect("/rol/redireccionar");
    }

}

