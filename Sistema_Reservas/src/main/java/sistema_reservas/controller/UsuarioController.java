package sistema_reservas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import sistema_reservas.model.Rol;
import sistema_reservas.model.Usuario;
import sistema_reservas.repository.RolRepository;
import sistema_reservas.repository.UsuarioRepository;

@Controller
public class UsuarioController {

	@Autowired
    private UsuarioRepository usuarioRepository;

	@Autowired
	private RolRepository rolRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Mostrar formulario de registro
    @GetMapping("/login/registroUsuario")
    public String mostrarRegistro(HttpServletRequest request, Model model) {
        model.addAttribute("currentPath", request.getRequestURI());
        return "login/registroUsuario";
    }

    // Procesar formulario de registro
    @PostMapping("/login/registroUsuario")
    public String procesarRegistro(@RequestParam("nombre") String nombre,
                                   @RequestParam("apellido") String apellido,
                                   @RequestParam("dni") Integer dni,
                                   @RequestParam("correo") String correo,
                                   @RequestParam("password") String password,
                                   Model model) {

        // Verifica si el correo ya está registrado
        //if (usuarioRepository.findByCorreo(correo).isPresent()) {
          //  model.addAttribute("error", "El correo ya está registrado.");
          //  return "registroUsuario";
       //}

        // Buscar el rol "CLIENTE" en la tabla roles
        Rol rolCliente = rolRepository.findByNombre("CLIENTE"); // debe devolver un Rol válido

        // Crear el nuevo usuario
        Usuario nuevo = new Usuario();
        nuevo.setNombre(nombre);
        nuevo.setApellido(apellido);
        nuevo.setDni(dni);
        nuevo.setCorreo(correo);
        nuevo.setPassword(passwordEncoder.encode(password));
        nuevo.setRol(rolCliente); // aquí se asigna el objeto Rol
        System.out.println("Registrando usuario: " + nuevo.getCorreo() + ", Rol: " + nuevo.getRol().getNombre());

        usuarioRepository.save(nuevo);

        return "redirect:/login";
    }

    
    //REDIRECCIONAR SEGUN ROL DEL USUARIO
    
    @GetMapping("/rol/redireccionar")
    public String redireccionSegunRol(Authentication auth) {
        System.out.println("Autenticación: " + auth);
        if (auth != null) {
            System.out.println("Autoridades: " + auth.getAuthorities());
            if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                System.out.println("Redirigiendo a admin...");
                return "redirect:/admin/inicio";
            } else if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CLIENTE"))) {
                System.out.println("Redirigiendo a cliente...");
                return "redirect:/cliente/inicio";
            }
        }
        System.out.println("Redirigiendo a login con error...");
        return "redirect:/login?error";
    }
}
