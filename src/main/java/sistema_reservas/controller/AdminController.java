package sistema_reservas.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import sistema_reservas.dto.HabitacionListadoDto;
import sistema_reservas.model.Usuario;
import sistema_reservas.repository.EstadoHabitacionRepository;
import sistema_reservas.repository.EstadoReservaRepository;
import sistema_reservas.repository.RolRepository;
import sistema_reservas.repository.TipoHabitacionDtoRepository;
import sistema_reservas.service.HabitacionService;
import sistema_reservas.service.UsuarioService;

@Controller
public class AdminController {

	@Autowired
	private UsuarioService usuarioService;
	@Autowired
	private RolRepository rolRepository;
    @Autowired
    private HabitacionService habitacionService;
    @Autowired
    private TipoHabitacionDtoRepository tipoHabitacionDtoRepository;
    @Autowired
    private EstadoReservaRepository estadoReservaRepository;
    @Autowired
    private EstadoHabitacionRepository estadoHabitacionRepository;

	@GetMapping("/admin/inicio")
    public String inicioAdmin(
            @RequestParam(required = false) Integer tipoHabId,
            @RequestParam(required = false) Integer estadoHabId,
            @RequestParam(required = false) Integer estadoReservaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            HttpServletRequest request, Model model) {
        model.addAttribute("currentPath", request.getRequestURI());

        List<HabitacionListadoDto> habitaciones = habitacionService.obtenerHabitacionesFiltro(
                tipoHabId, estadoHabId, estadoReservaId,
                fechaInicio != null ? java.sql.Date.valueOf(fechaInicio) : null,
                fechaFin != null ? java.sql.Date.valueOf(fechaFin) : null
        );

        model.addAttribute("habitaciones", habitaciones);

        model.addAttribute("tipos", tipoHabitacionDtoRepository.findAll());
        model.addAttribute("estadosHab", estadoHabitacionRepository.findAll());
        model.addAttribute("estadosReserva", estadoReservaRepository.findAll());

        return "admin/adminInicio";
    }
	
	@GetMapping("/admin/consultas")
    public String gestionConsultas(HttpServletRequest request, Model model) {
        model.addAttribute("currentPath", request.getRequestURI());
        return "admin/adminConsultas";
    }

    @GetMapping("/admin/usuarios")
    public String gestionUsuarios(
            @RequestParam(required = false) Integer rolid,
    		HttpServletRequest request, Model model) {
    	
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("usuarios",usuarioService.obtenerUsuarios(rolid));
        model.addAttribute("roles", rolRepository.findAll());
        model.addAttribute("rolSeleccionado", rolid);
        
        return "admin/adminUsuarios";
    }

    @GetMapping("/admin/mantenimientos")
    public String gestionMantenimientos(HttpServletRequest request, Model model) {
        model.addAttribute("currentPath", request.getRequestURI());
        return "admin/adminHabitaciones";
    }

    @GetMapping("/admin/reservas")
    public String gestionReservas(HttpServletRequest request, Model model) {
        model.addAttribute("currentPath", request.getRequestURI());
        return "admin/adminReservas";
    }

    @GetMapping("/admin/perfil")
    public String gestionPerfil(
            HttpSession session,
            HttpServletRequest request, Model model){
        model.addAttribute("currentPath", request.getRequestURI());

        Integer id = (Integer) session.getAttribute("usuarioId");
        if(id == null) return "redirect:/login";

        Usuario perfil = usuarioService.obtenerPerfil(id);
        model.addAttribute("perfil", perfil);

        return "admin/adminPerfil";
    }
    
    @GetMapping("/admin/perfil/editarPerfil")
    public String gestionEditarPerfil(HttpServletRequest request, Model model, Principal principal) {
        model.addAttribute("currentPath", request.getRequestURI());

        String correo = principal.getName();
        Usuario perfil = usuarioService.obtenerPerfilPorCorreo(correo);
        model.addAttribute("perfil", perfil);
        
        return "admin/adminEditarPerfil";
    }
    
    @PostMapping("/admin/perfil/guardar")
    public String guardarPerfil(
            @ModelAttribute Usuario usuario,
    		HttpServletRequest request, Model model) {
    	model.addAttribute("currentPath", request.getRequestURI());

        usuarioService.actualizarPerfil(usuario);
        
        return "redirect:/admin/perfil";
    }
}
