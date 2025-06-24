package sistema_reservas.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sistema_reservas.dao.HabitacionDao;
import sistema_reservas.dto.HabitacionDto;
import sistema_reservas.dto.PisoDto;
import sistema_reservas.model.TipoHabitacion;
import sistema_reservas.repository.TipoHabitacionRepository;

@Controller
public class ClienteController {

    @Autowired
    private TipoHabitacionRepository tipoHabitacionRepository;


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
    public String misReservas(HttpServletRequest request, Model model) {
        model.addAttribute("currentPath", request.getRequestURI());
        return "cliente/clienteMisReservas"; // aún por crear
    }

    @GetMapping("/cliente/perfil")
    public String perfil(HttpServletRequest request, Model model) {
        model.addAttribute("currentPath", request.getRequestURI());
        return "cliente/clientePerfil"; // aún por crear
    }
}
