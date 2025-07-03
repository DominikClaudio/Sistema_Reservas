package sistema_reservas.login;

import java.io.IOException;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sistema_reservas.model.Usuario;
import sistema_reservas.repository.UsuarioRepository;
import sistema_reservas.service.RabbitMQProducer;

@Component
public class ManejadorLogin implements AuthenticationSuccessHandler {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RabbitMQProducer rabbitMQProducer; // <-- Inyecta el producer

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        System.out.println("LOGIN ÉXITOSO para: " + authentication.getName());

        String correo = authentication.getName();
        Usuario usuario = usuarioRepository.findByCorreo(correo).orElse(null);

        // Envia mensaje a RabbitMQ despues del registro
        rabbitMQProducer.sendMessage(
                "Usuario Logeado: " +  usuario.getCorreo() + " - " + usuario.getNombre() + " " + usuario.getApellido()
        );
        if (usuario != null) {
            request.getSession().setAttribute("usuarioId", usuario.getIdusuario());
            request.getSession().setAttribute("usuarioNombre", usuario.getNombre());
            HttpSession session = request.getSession();
            session.setAttribute("usuario", usuario);
        }

        response.sendRedirect("/rol/redireccionar");
    }

}

