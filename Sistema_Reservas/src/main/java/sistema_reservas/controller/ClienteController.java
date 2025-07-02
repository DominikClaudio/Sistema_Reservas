package sistema_reservas.controller;

import java.security.Principal;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sistema_reservas.dao.HabitacionDao;
import sistema_reservas.dto.HabitacionDto;
import sistema_reservas.dto.PisoDto;
import sistema_reservas.model.Reserva;
import sistema_reservas.model.TipoHabitacion;
import sistema_reservas.model.Usuario;
import sistema_reservas.repository.TipoHabitacionRepository;
import sistema_reservas.repository.UsuarioRepository;
import sistema_reservas.service.ReservaService;

@Controller
public class ClienteController {

    @Autowired
    private TipoHabitacionRepository tipoHabitacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private ReservaService reservaService;


    @Autowired
    private HabitacionDao habitacionDao;

	@GetMapping("/cliente/inicio")
    public String inicioCliente(HttpServletRequest request, Model model) {
        model.addAttribute("currentPath", request.getRequestURI());

        List<TipoHabitacion> tipos = tipoHabitacionRepository.findAll();
        model.addAttribute("tipos", tipos);

        return "cliente/clienteInicio";
    }

    @GetMapping("/cliente/habitaciones")
    public String habitacionesCliente(HttpServletRequest request, Model model) {
        model.addAttribute("currentPath", request.getRequestURI());

        List<TipoHabitacion> tipos = tipoHabitacionRepository.findAll();
        model.addAttribute("tipos", tipos);
        
        return "cliente/clienteHabitaciones";
    }

    @PostMapping("/cliente/seleccionar-fechas")
    public String seleccionarFechas(
            @RequestParam("tipohabId") int tipohabId,
                    HttpServletRequest request,
                    Model model) {

        // Fechas por defecto
        LocalDate fechaEntrada = LocalDate.now();
        LocalDate fechaSalida = fechaEntrada.plusDays(1);

        int adultos = 2;
        int ninos = 0;
        int cantidadPersonas = adultos + ninos;

        List<PisoDto> pisos = habitacionDao.listarPisosPorTipo(tipohabId);

        Integer pisoSeleccionado = !pisos.isEmpty() ? pisos.get(0).getIdpiso() : null;

        List<HabitacionDto> habitaciones = habitacionDao.listarHabitacionesDisponibles(
                fechaEntrada, fechaSalida, tipohabId, pisoSeleccionado, cantidadPersonas
        );

        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("tipohabId", tipohabId);
        model.addAttribute("pisos", pisos);
        model.addAttribute("fechaEntrada", fechaEntrada);
        model.addAttribute("fechaSalida", fechaSalida);
        model.addAttribute("pisoSeleccionado", pisoSeleccionado);
        model.addAttribute("habitaciones", habitaciones);
        model.addAttribute("adultos", adultos);
        model.addAttribute("ninos", ninos);
        model.addAttribute("capacidadPersonas", cantidadPersonas);

        List<TipoHabitacion> tipos = tipoHabitacionRepository.findAll();
        model.addAttribute("tipos", tipos);

        return "cliente/clienteHabitaciones";
    }

    @PostMapping("/cliente/filtrar-habitaciones")
    public String filtrarHabitaciones(
            @RequestParam("tipohabId") int tipohabId,
            @RequestParam("fechaEntrada") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaEntrada,
            @RequestParam("fechaSalida") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaSalida,
            @RequestParam("pisoId") int pisoId,
            @RequestParam(value = "adultos") int adultos,
            @RequestParam(value = "ninos", required = false, defaultValue = "0") Integer ninos,
            HttpServletRequest request,
            Model model
    ) {
        int cantidadPersonas = adultos + ninos;

        List<PisoDto> pisos = habitacionDao.listarPisosPorTipo(tipohabId);
        List<HabitacionDto> habitaciones = habitacionDao.listarHabitacionesDisponibles(
                fechaEntrada, fechaSalida, tipohabId, pisoId, cantidadPersonas);

        if ((adultos + ninos) > 2) {
            model.addAttribute("capacidadExcedida", true);
            habitaciones = new ArrayList<>();
        }

        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("tipohabId", tipohabId);
        model.addAttribute("pisos", pisos);
        model.addAttribute("fechaEntrada", fechaEntrada);
        model.addAttribute("fechaSalida", fechaSalida);
        model.addAttribute("pisoSeleccionado", pisoId);
        model.addAttribute("habitaciones", habitaciones);
        model.addAttribute("capacidadPersonas", cantidadPersonas);

        return "cliente/clienteHabitaciones"; // misma vista
    }

    @GetMapping("/cliente/mi-seleccion")
    public String miSeleccion(HttpServletRequest request, Model model) {
        model.addAttribute("currentPath", request.getRequestURI());
        return "cliente/clienteMiSeleccion"; // aún por crear
    }

    @GetMapping("/cliente/mis-reservas")
    public String misReservas(HttpServletRequest request, Model model, Principal principal) {
        // Ruta actual para navegación activa
        model.addAttribute("currentPath", request.getRequestURI());

        // Listar las reservas del usuario logueado
        String correo = principal.getName();
        List<Reserva> reservas = reservaService.listarReservasPorUsuario(correo);
        model.addAttribute("reservas", reservas);

        return "cliente/clienteMisReservas";
    }
    @GetMapping("/cliente/perfil")
    public String perfil(HttpServletRequest request, Model model, Principal principal) {
        model.addAttribute("currentPath", request.getRequestURI());

        // El principal.getName() devuelve el correo que usaste para autenticar.
        String correo = principal.getName();


        // 3) buscar en el repositorio
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);
        

        if (usuarioOpt.isPresent()) {
            model.addAttribute("perfil", usuarioOpt.get());
        } else {
            // redirigir a creación de perfil o mostrar mensaje
            return "redirect:/cliente/crearPerfil";
        }

        return "cliente/clientePerfil"; // aún por crear
    }
    /**
     * Muestra la página de confirmación de reserva.
     */
    @RequestMapping(
    		  value = "/cliente/confirmar-reserva",
    		  method = { RequestMethod.GET, RequestMethod.POST }
    		)
    public String confirmarReserva(
            HttpServletRequest request,
            Model model,
            @RequestParam("tipohabId") int tipohabId,
            @RequestParam(value = "fechaEntrada", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaEntrada,
            @RequestParam(value = "fechaSalida", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaSalida,
            @RequestParam("pisoId") int pisoId,
            @RequestParam(value ="adultos", required = false) Integer adultos,
            @RequestParam(value ="ninos", required = false) Integer ninos,
            Principal principal) { 

        // Si no se pasó fechaEntrada, usar hoy
        if (fechaEntrada == null) {
            fechaEntrada = LocalDate.now();
        }
        // Lo mismo para fechaSalida si quisieras
        if (fechaSalida == null) {
            fechaSalida = fechaEntrada.plusDays(1);
        }

        if (adultos == null) {
        	adultos = 1;
        }
        if (ninos == null) {
        	ninos = 1;
        }
        // 1) Ruta para nav
        model.addAttribute("currentPath", request.getRequestURI());

        // 2) Recuperar datos del usuario
        String correo = principal.getName();
        

        // 3) buscar en el repositorio
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);
        

        if (usuarioOpt.isPresent()) {
            model.addAttribute("perfil", usuarioOpt.get());
        } else {
            // redirigir a creación de perfil o mostrar mensaje
            return "redirect:/";
        }
        Usuario usuario = usuarioRepository.findByCorreo(correo)
            .orElseThrow();
        model.addAttribute("perfil", usuario);

        // 3) Volcar en el modelo todo lo necesario para confirmar
        model.addAttribute("tipohabId", tipohabId);
        model.addAttribute("fechaEntrada", fechaEntrada);
        model.addAttribute("fechaSalida", fechaSalida);
        model.addAttribute("pisoId", pisoId);
        model.addAttribute("adultos", adultos);
        model.addAttribute("ninos", ninos);

        // 4) Cualquier otra cosa (precio, calculadora, etc.)
        //    Puedes calcular aquí el total, disponibilidad final, etc.
        //    model.addAttribute("total", total);


        return /*
        request.getMethod().equals("GET")
             ? "cliente/confirmarReserva"
             : */this.guardarReserva(tipohabId,fechaEntrada,fechaSalida,pisoId,adultos,ninos,principal) ; // plantilla Thymeleaf
    }
    @PostMapping("/cliente/guardar-reserva")
    public String guardarReserva(
            @RequestParam("tipohabId") int tipohabId,
            @RequestParam("fechaEntrada") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaEntrada,
            @RequestParam("fechaSalida") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaSalida,
            @RequestParam("pisoId") int pisoId,
            @RequestParam("adultos") int adultos,
            @RequestParam("ninos") int ninos,
            Principal principal) {

        // Lógica para persistir la reserva (Servicio / DAO)
        reservaService.crearReserva(principal.getName(), tipohabId, fechaEntrada, fechaSalida, pisoId, adultos, ninos);

        // redirigir a “mis reservas” u otra vista
        return "redirect:/cliente/mis-reservas";
    }
    
/*
    // 1) Soportar GET para seleccionar-fechas (redirige al listado de habitaciones)
    @GetMapping("/seleccionar-fechas")
    public String seleccionarFechasGet() {
        return "redirect:/cliente/habitaciones";
    }

    // 2) Asegúrate de que el método POST devuelve la vista correcta
    @PostMapping("/seleccionar-fechas")
    public String seleccionarFechas( // parámetros  ) {
        // tu lógica existente...
        return "cliente/clienteHabitaciones";
    }

    // 3) Mi Selección
    @GetMapping("/mi-seleccion")
    public String miSeleccion(Model model) {
        // carga “seleccion” en el model según tu lógica (session o servicio)
        return "cliente/clienteMiseleccion";
    }

    // 4) Mis Reservas
    @GetMapping("/mis-reservas")
    public String misReservas(Model model) {
        // model.addAttribute("reservas", servicio.obtenerReservas(...));
        return "cliente/clienteMisReservas";
    }

    // 5) Perfil
    @GetMapping("/perfil")
    public String perfil(Model model, Principal principal) {
        //String correo = principal.getName();
        //Usuario perfil = usuarioService.obtenerPerfilPorCorreo(correo);
        // model.addAttribute("perfil", perfil);
        return "cliente/clientePerfil";
    }

    // 6) Editar perfil (GET + POST)
    @GetMapping("/perfil/editar")
    public String editarPerfil(Model model, Principal principal) {
        String correo = principal.getName();
        // Usuario perfil = ...
        // model.addAttribute("perfil", perfil);
        return "cliente/clienteEditarPerfil"; // crea también esta vista si la deseas
    }
    @PostMapping("/perfil/guardar")
    public String guardarPerfil(// @ModelAttribute Usuario u ) {
        // actualiza perfil
        return "redirect:/cliente/perfil";
    }
    */
}
