package sistema_reservas.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sistema_reservas.dao.ClienteDao;
import sistema_reservas.dao.HabitacionDao;
import sistema_reservas.dto.*;
import sistema_reservas.model.Habitaciones;
import sistema_reservas.model.TipoHabitacion;
import sistema_reservas.model.Usuario;
import sistema_reservas.repository.HabitacionRepository;
import sistema_reservas.repository.TipoHabitacionRepository;
import sistema_reservas.feign.WeatherClient;
import sistema_reservas.service.CorreoProducerService;
import sistema_reservas.service.UsuarioService;

@Controller
public class ClienteController {

    @Autowired
    private TipoHabitacionRepository tipoHabitacionRepository;
    @Autowired
    private HabitacionDao habitacionDao;
    @Autowired
    private ClienteDao clienteDao;
    @Autowired
    private HabitacionRepository habitacionRepository;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private CorreoProducerService correoProducerService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private WeatherClient weatherClient;
    private final String API_KEY = "28d1a4616c67509a76beb8c9fabf0c8d";

    @GetMapping("/cliente/inicio")
    public String inicioCliente(HttpServletRequest request, Model model) {
        model.addAttribute("currentPath", request.getRequestURI());

        List<TipoHabitacion> tipos = tipoHabitacionRepository.findAll();
        model.addAttribute("tipos", tipos);


        try {
            Map<String, Object> datosClima = weatherClient.obtenerClimaPorCiudad("Lima", API_KEY, "metric", "es");


            Map<String, Object> main = (Map<String, Object>) datosClima.get("main");
            List<Map<String, Object>> weatherList = (List<Map<String, Object>>) datosClima.get("weather");
            Map<String, Object> weather = weatherList.get(0);

            model.addAttribute("temp", main.get("temp"));
            model.addAttribute("descripcion", weather.get("description"));
            model.addAttribute("icono", weather.get("icon"));
            model.addAttribute("ciudad", datosClima.get("name"));
        } catch (Exception e) {
            model.addAttribute("temp", "N/D");
            model.addAttribute("descripcion", "Sin datos");
            model.addAttribute("icono", null);
            model.addAttribute("ciudad", "Ciudad desconocida");
        }

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
    public String miSeleccion(
            HttpSession session,
            HttpServletRequest request, Model model) {

        List<HabitacionSeleccionadaDto> seleccion =
                (List<HabitacionSeleccionadaDto>) session.getAttribute("miSeleccion");

        if (seleccion == null) seleccion = new ArrayList<>();

        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("habitacionesSeleccionadas", seleccion);

        System.out.println(">> Lista en sesión:");
        seleccion.forEach(h -> System.out.println(h.getNumHabitacion() + " - " + h.getFechaEntrada()));

        return "cliente/clienteMiseleccion";
    }

    @PostMapping("/cliente/reservar-habitacion")
    public String reservarHabitacionIndividual(
            @RequestParam("habitacionId") int habitacionId,
            @RequestParam("fechaEntrada") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaEntrada,
            @RequestParam("fechaSalida") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaSalida,
            HttpSession session,
            RedirectAttributes redirect
    ) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            redirect.addFlashAttribute("error", "Tu sesión ha expirado. Por favor inicia sesión nuevamente.");
            return "redirect:/login";
        }

        int usuarioId = usuario.getIdusuario();
        int estadoId = 2;
        String listaHabitaciones = String.valueOf(habitacionId);

        // NUEVO: devuelve lista de reservas
        List<Integer> idsReserva = habitacionDao.registrarReservaConDetalles(
                usuarioId,
                fechaEntrada,
                fechaSalida,
                estadoId,
                listaHabitaciones
        );

        if (idsReserva == null || idsReserva.isEmpty()) {
            redirect.addFlashAttribute("error", "Ocurrió un error al registrar la reserva.");
            return "redirect:/cliente/habitaciones";
        }

        // ✅ Notificación para el admin (puedes personalizar según si es 1 o muchas)
        String mensaje = "Nueva reserva realizada por " + usuario.getNombre() + " " + usuario.getApellido();
        messagingTemplate.convertAndSend("/tema/notificacion", mensaje);

        // ✅ Enviar correo por cada reserva realizada
        for (Integer reservaId : idsReserva) {
            DetalleCorreoDto detalle = clienteDao.obtenerDetalleCorreo(reservaId);
            correoProducerService.enviarCorreoReserva(detalle);
        }

        redirect.addFlashAttribute("exito", "Reserva realizada con éxito.");
        return "redirect:/cliente/mis-reservas";
    }


    @PostMapping("/cliente/agregar-seleccion")
    public String agregarSeleccion(
            @RequestParam("habitacionId") int habitacionId,
            @RequestParam("fechaEntrada") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaEntrada,
            @RequestParam("fechaSalida") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaSalida,
            HttpSession session,
            RedirectAttributes redirect
    ) {
        System.out.println(">> ID recibido: " + habitacionId);

        List<HabitacionSeleccionadaDto> seleccion = (List<HabitacionSeleccionadaDto>) session.getAttribute("miSeleccion");
        if (seleccion == null) {
            seleccion = new ArrayList<>();
        }

        if (seleccion.size() >= 3) {
            redirect.addFlashAttribute("error", "Solo puedes agregar hasta 3 habitaciones.");
            return "redirect:/cliente/habitaciones";
        }

        boolean yaExiste = seleccion.stream().anyMatch(h -> h.getIdhabitacion() == habitacionId);
        if (yaExiste) {
            redirect.addFlashAttribute("error", "Ya agregaste esta habitación.");
            return "redirect:/cliente/habitaciones";
        }

        System.out.println("ID recibido: " + habitacionId);
        Habitaciones hab = habitacionRepository.findById(habitacionId).orElse(null);
        System.out.println("Objeto recuperado: " + hab);

        if (hab == null) {
            redirect.addFlashAttribute("error", "Habitación no encontrada.");
            return "redirect:/cliente/habitaciones";
        }

        HabitacionSeleccionadaDto dto = new HabitacionSeleccionadaDto();
        dto.setIdhabitacion(habitacionId);
        dto.setTipoHabitacion(hab.getTipoHabitacion().getNombre());
        dto.setNumHabitacion(hab.getNumhabitacion());
        dto.setPiso(hab.getPisos().getNombre());
        dto.setFechaEntrada(fechaEntrada);
        dto.setFechaSalida(fechaSalida);

        int dias = (int) ChronoUnit.DAYS.between(fechaEntrada, fechaSalida);
        dto.setCantidadDias(dias);
        dto.setPrecioxDia(hab.getTipoHabitacion().getPrecioxDia());
        dto.setTotal(dto.getPrecioxDia() * dias);

        seleccion.add(dto);
        session.setAttribute("miSeleccion", seleccion);

        System.out.println(">> AGREGANDO habitación ID: " + habitacionId);

        redirect.addFlashAttribute("exito", "Habitación agregada a tu selección.");

        return "redirect:/cliente/mi-seleccion";
    }

    @PostMapping("/cliente/eliminar-seleccion")
    public String eliminarDeSeleccion(
            @RequestParam("habitacionId") int habitacionId,
            HttpSession session,
            RedirectAttributes redirect) {

        List<HabitacionSeleccionadaDto> seleccion =
                (List<HabitacionSeleccionadaDto>) session.getAttribute("miSeleccion");

        if (seleccion != null) {
            seleccion.removeIf(h -> h.getIdhabitacion() == habitacionId);
            session.setAttribute("miSeleccion", seleccion);
            redirect.addFlashAttribute("exito", "Habitación eliminada.");
        }

        return "redirect:/cliente/mi-seleccion";
    }

    @PostMapping("/cliente/reservar-seleccion")
    public String reservarSeleccionCompleta(HttpSession session, RedirectAttributes redirect) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        List<HabitacionSeleccionadaDto> seleccion = (List<HabitacionSeleccionadaDto>) session.getAttribute("miSeleccion");
        if (seleccion == null || seleccion.isEmpty()) {
            redirect.addFlashAttribute("error", "No tienes habitaciones seleccionadas.");
            return "redirect:/cliente/mi-seleccion";
        }

        LocalDate fechaEntrada = seleccion.get(0).getFechaEntrada();
        LocalDate fechaSalida = seleccion.get(0).getFechaSalida();
        boolean fechasIguales = seleccion.stream().allMatch(h ->
                h.getFechaEntrada().equals(fechaEntrada) && h.getFechaSalida().equals(fechaSalida)
        );
        if (!fechasIguales) {
            redirect.addFlashAttribute("error", "Todas las habitaciones deben tener la misma fecha de entrada y salida.");
            return "redirect:/cliente/mi-seleccion";
        }

        // 👉 Aquí aplicamos el distinct()
        String listaHabitaciones = seleccion.stream()
                .map(h -> h.getIdhabitacion())
                .distinct() // ✅ Evita habitaciones duplicadas
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        int estadoId = 2;

        // 👇 CAMBIO: ahora obtenemos una lista de IDs
        List<Integer> idsReserva = habitacionDao.registrarReservaConDetalles(
                usuario.getIdusuario(),
                fechaEntrada,
                fechaSalida,
                estadoId,
                listaHabitaciones
        );

        if (idsReserva == null || idsReserva.isEmpty()) {
            redirect.addFlashAttribute("error", "Ocurrió un error al registrar la reserva.");
            return "redirect:/cliente/mi-seleccion";
        }

        // ✅ Enviar notificación
        String mensaje = "El cliente " + usuario.getNombre() + " " + usuario.getApellido() +
                " ha realizado una reserva múltiple de " + seleccion.size() + " habitaciones.";
        messagingTemplate.convertAndSend("/tema/notificacion", mensaje);

        // ✅ Enviar correo por cada reserva registrada
        for (Integer id : idsReserva) {
            DetalleCorreoDto detalle = clienteDao.obtenerDetalleCorreo(id);
            correoProducerService.enviarCorreoReserva(detalle);
        }

        // ✅ Limpiar selección
        session.removeAttribute("miSeleccion");

        redirect.addFlashAttribute("exito", "Se realizaron " + idsReserva.size() + " reservas con éxito.");
        return "redirect:/cliente/mis-reservas";
    }


    @GetMapping("/cliente/mis-reservas")
    public String misReservas(
            HttpSession session,
            HttpServletRequest request, Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if(usuario == null){
            return "redirect:/login";
        }

        List<ReservaDto> reservas = habitacionDao.listarMisReservas(usuario.getIdusuario());

        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("reservas", reservas);
        return "cliente/clienteMisreservas";
    }

    @PostMapping("/cliente/cancelar-reserva")
    public String cancelarReserva(@RequestParam("reservaId") int reservaId, RedirectAttributes redirect) {

        habitacionDao.cancelarReserva(reservaId);
        redirect.addFlashAttribute("exito", "Reserva cancelada correctamente.");
        return "redirect:/cliente/mis-reservas";
    }

    @GetMapping("/cliente/detalle-reserva/{id}")
    public String mostrarDetalleReserva(@PathVariable("id") int id, Model model) {
        ReservaListadoDto detalle = clienteDao.obtenerDetalleReserva(id);
        model.addAttribute("reserva", detalle);
        return "fragments/modalDetalle :: modal-contenido";
    }

    @GetMapping("/cliente/perfil")
    public String gestionPerfil(
            HttpSession session,
            HttpServletRequest request, Model model){
        model.addAttribute("currentPath", request.getRequestURI());

        Integer id = (Integer) session.getAttribute("usuarioId");
        if(id == null) return "redirect:/login";

        Usuario perfil = usuarioService.obtenerPerfil(id);
        model.addAttribute("perfil", perfil);

        return "cliente/clientePerfil";
    }

    @GetMapping("/cliente/editar-Perfil")
    public String gestionEditarPerfil(HttpServletRequest request, Model model, Principal principal) {
        model.addAttribute("currentPath", request.getRequestURI());

        String correo = principal.getName();
        Usuario perfil = usuarioService.obtenerPerfilPorCorreo(correo);
        model.addAttribute("perfil", perfil);

        return "cliente/clienteEditarPerfil";
    }

    @PostMapping("/cliente/guardar-perfil")
    public String guardarPerfil(
            @ModelAttribute Usuario usuario,
            HttpServletRequest request, Model model) {
        model.addAttribute("currentPath", request.getRequestURI());

        usuarioService.actualizarPerfil(usuario);

        return "redirect:/cliente/perfil";
    }
}
