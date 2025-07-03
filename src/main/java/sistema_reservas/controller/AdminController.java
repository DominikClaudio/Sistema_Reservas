package sistema_reservas.controller;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import feign.FeignException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sistema_reservas.dao.AdminDao;
import sistema_reservas.dao.HabitacionDao;
import sistema_reservas.dto.*;
import sistema_reservas.model.*;
import sistema_reservas.repository.*;
import sistema_reservas.feign.HabitacionClient;
import sistema_reservas.feign.PisoClient;
import sistema_reservas.feign.TipoHabitacionClient;
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
    @Autowired
    private HabitacionDao habitacionDao;
    @Autowired
    private AdminDao adminDao;

    @Autowired
    private HabitacionClient habitacionClient;
    @Autowired
    private PisoClient pisoClient;
    @Autowired
    private TipoHabitacionClient tipoHabitacionClient;
    @Autowired
    private HabitacionRepository habitacionRepository;

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

    @GetMapping("/admin/editar-estado/{id}")
    public String mostrarFormularioEstado(HttpServletRequest request, @PathVariable int id, Model model) {
        Habitaciones h = habitacionRepository.findById(id).orElseThrow();
        List<EstadoHabitacion> estados = estadoHabitacionRepository.findAll();

        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("habitacion", h);
        model.addAttribute("estadosHab", estados);

        return "admin/adminActualizarEstadoHab";
    }

    @PostMapping("/admin/actualizar-estadoHab")
    public String actualizarEstadoHabitacion(
            @RequestParam int idHabitacion,
            @RequestParam int estadoHabId,
            RedirectAttributes redirect){

        try {
            adminDao.actualizarEstadoHabitacion(idHabitacion, estadoHabId);
            redirect.addFlashAttribute("exito", "Estado actualizado correctamente.");
        } catch (Exception e){
            redirect.addFlashAttribute("error", "No pudo actualizar el estado");
        }

        return "redirect:/admin/inicio";
    }

	@GetMapping("/admin/consultas")
    public String gestionConsultas(
            HttpServletRequest request, Model model) {
        model.addAttribute("currentPath", request.getRequestURI());
        return "admin/adminConsultas";
    }

    @PostMapping("/admin/consultas")
    public String buscarReservaPorCodigo(
            @RequestParam("codigoReserva") String codigo,
            RedirectAttributes redirect
    ) {
        List<ReservaListadoDto> resultados = habitacionDao.buscarPorCodigoReserva(codigo.trim());

        redirect.addFlashAttribute("resultados", resultados);
        redirect.addFlashAttribute("codigoBuscado", codigo);

        return "redirect:/admin/consultas";
    }

    @PostMapping("/admin/confirmar-reserva")
    public String confirmarReserva(
            @RequestParam("reservaId") Integer reservaId, RedirectAttributes redirect){

        adminDao.confirmarReserva(reservaId);
        redirect.addFlashAttribute("exito", "Reserva confirmada correctamente");

        return "redirect:/admin/consultas";
    }

    @PostMapping("/admin/finalizar-reserva")
    public String finalizarReserva(
            @RequestParam("reservaId") Integer reservaId, RedirectAttributes redirect){

        adminDao.finalizarReserva(reservaId);
        redirect.addFlashAttribute("exito", "Reserva finalizada correctamente");

        return "redirect:/admin/consultas";
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

    @GetMapping("/admin/editar-rol/{id}")
    public String mostrarFormularioEdicion(
            HttpServletRequest request,
            @PathVariable("id") int id, Model model) {
        UsuarioListadoDto usuario = adminDao.obtenerUsuarioPorId(id);
        List<RolDto> roles = adminDao.listarRoles();

        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", roles);
        return "admin/adminEditarUsuario";
    }

    @PostMapping("/admin/actualizar-rol")
    public String actualizarRolUsuario(@RequestParam int idUsuario, @RequestParam int rolId) {
        adminDao.actualizarRolUsuario(idUsuario, rolId);
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/admin/mantHabitacion")
    public String gestionMantenimientos(HttpServletRequest request, Model model) {

        List<Habitaciones> lista = habitacionClient.listarHabitaciones();

        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("habitaciones", lista);
        return "admin/mantHabitacion";
    }

    @GetMapping("/admin/mantHabitacion/nuevo")
    public String nuevaHabitacion(HttpServletRequest request, Model model){

        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("habitacion", new Habitaciones());
        model.addAttribute("pisos", pisoClient.listarPisos());
        model.addAttribute("tipos", tipoHabitacionClient.listarTipos());

        return "admin/mantHabRegistrarEditar";
    }

    @PostMapping("/admin/mantHabitacion/guardar")
    public String guardarHabitacion(
            @ModelAttribute("habitacion") Habitaciones hab,
            RedirectAttributes redirect){

        habitacionClient.guardarHabitacion(hab);
        redirect.addFlashAttribute("exito", "Habitación registrada con éxito.");

        return "redirect:/admin/mantHabitacion";
    }

    @GetMapping("/admin/mantHabitacion/editar/{id}")
    public String editarHabitacion(HttpServletRequest request,@PathVariable("id") int id, Model model){

        Habitaciones hab = habitacionClient.obtenerPorIdHabitacion(id);

        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("habitacion", hab);
        model.addAttribute("pisos", pisoClient.listarPisos());
        model.addAttribute("tipos", tipoHabitacionClient.listarTipos());

        return "admin/mantHabRegistrarEditar";
    }

    @GetMapping("/admin/mantHabitacion/eliminar/{id}")
    public String eliminarHabitacion(@PathVariable("id") int id, RedirectAttributes redirect){
        habitacionClient.eliminarHabitacion(id);

        redirect.addFlashAttribute("exito", "Habitación eliminada.");

        return "redirect:/admin/mantHabitacion";
    }


    @GetMapping("/admin/mantPiso")
    public String listarPisos(HttpServletRequest request, Model model) {

        List<Pisos> lista = pisoClient.listarPisos();

        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("pisos", lista);

        return "admin/mantPiso";
    }

    @GetMapping("/admin/mantPiso/nuevo")
    public String nuevoPiso(HttpServletRequest request, Model model){

        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("piso", new Pisos());

        return "admin/mantPisoRegistrarEditar";
    }

    @PostMapping("/admin/mantPiso/guardar")
    public String guardarPiso(@ModelAttribute("piso") Pisos piso, RedirectAttributes redirect) {

        pisoClient.guardarPiso(piso);
        redirect.addFlashAttribute("exito", "Piso guardado con éxito.");

        return "redirect:/admin/mantPiso";
    }

    @GetMapping("/admin/mantPiso/editar/{id}")
    public String editarPiso(
            HttpServletRequest request,
            @PathVariable("id") int id, Model model) {

        Pisos piso = pisoClient.obtenerPorIdPiso(id);

        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("piso", piso);

        return "admin/mantPisoRegistrarEditar";
    }

    @GetMapping("/admin/mantPiso/eliminar/{id}")
    public String eliminarPiso(@PathVariable("id") int id, RedirectAttributes redirect) {

        try {
            pisoClient.eliminarPiso(id);
            redirect.addFlashAttribute("exito", "Piso eliminado correctamente.");
        } catch (FeignException.Conflict e){
            redirect.addFlashAttribute("error", "No se puede eliminar el piso porque tiene habitaciones asociadas.");
        } catch (FeignException e){
            redirect.addFlashAttribute("error", "Ocurrió un error al intentar eliminar el piso.");
        }

        return "redirect:/admin/mantPiso";
    }

    @GetMapping("/admin/mantTipoHab")
    public String listarTipos(HttpServletRequest request, Model model) {

        List<TipoHabitacionBD> lista = tipoHabitacionClient.listarTipos();

        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("tipos", lista);

        return "admin/mantTipoHab";
    }

    @GetMapping("/admin/mantTipoHab/nuevo")
    public String nuevoTipo(HttpServletRequest request, Model model) {

        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("tipo", new TipoHabitacionBD());
        return "admin/mantTipoRegistrarEditar";
    }

    @PostMapping("/admin/mantTipoHab/guardar")
    public String guardarTipo(
            @ModelAttribute("tipo") TipoHabitacionBD tipo, RedirectAttributes redirect) {

        tipoHabitacionClient.guardarTipo(tipo);

        redirect.addFlashAttribute("exito", "Tipo de habitación guardado con éxito.");

        return "redirect:/admin/mantTipoHab";
    }

    @GetMapping("/admin/mantTipoHab/editar/{id}")
    public String editarTipo(HttpServletRequest request, @PathVariable("id") int id, Model model) {

        TipoHabitacionBD tipo = tipoHabitacionClient.obtenerPorIdTipo(id);

        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("tipo", tipo);

        return "admin/mantTipoRegistrarEditar";
    }

    @GetMapping("/admin/mantTipoHab/eliminar/{id}")
    public String eliminarTipo(@PathVariable("id") int id, RedirectAttributes redirect) {

        try {
            tipoHabitacionClient.eliminarTipo(id);
            redirect.addFlashAttribute("exito", "Tipo de habitación eliminado correctamente.");
        } catch (FeignException.Conflict e){
            redirect.addFlashAttribute("error", "No se puede eliminar el tipo de habitacion porque tiene habitaciones asociadas.");
        } catch (FeignException e){
            redirect.addFlashAttribute("error", "Ocurrió un error al intentar eliminar el tipo de habitación.");
        }

        return "redirect:/admin/mantTipoHab";
    }

    @GetMapping("/admin/reportesHabitaciones")
    public String gestionReportes(HttpServletRequest request, Model model) {

        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);

        List<ReporteReservasDto> reservas = adminDao.obtenerReporteReservasConfirmadas(inicioMes, hoy);
        int total = adminDao.obtenerTotalReservasConfirmadas(inicioMes, hoy);

        long dias = ChronoUnit.DAYS.between(inicioMes, hoy) + 1;

        String periodoTexto;
        if (dias == 1){
            periodoTexto = "un día";
        } else if (dias == 7) {
            periodoTexto = "una semana";
        } else if (dias == 30 || dias == 31) {
            periodoTexto = "un mes";
        }else {
            periodoTexto = dias + " días";
        }

        String resumen = "Total Habitaciones Reservadas | " + total + " ingresos durante " + periodoTexto;

        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("listaReservas", reservas);
        model.addAttribute("fechaEntrada", inicioMes);
        model.addAttribute("fechaSalida", hoy);
        model.addAttribute("totalResumen", resumen);

        return "admin/reporteHabitaciones";
    }

    @PostMapping("/admin/reservas-confirmadas")
    public String procesarReporteHabitaciones(
            @RequestParam("fechaEntrada") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaEntrada,
            @RequestParam("fechaSalida") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaSalida,
            Model model,
            HttpServletRequest request
    ) {
        model.addAttribute("currentPath", request.getRequestURI());

        if (fechaEntrada != null && fechaSalida != null){
            List<ReporteReservasDto> reservas = adminDao.obtenerReporteReservasConfirmadas(fechaEntrada, fechaSalida);
            int total = adminDao.obtenerTotalReservasConfirmadas(fechaEntrada, fechaSalida);

            long dias = ChronoUnit.DAYS.between(fechaEntrada, fechaSalida) + 1;

            String periodoTexto;
            if (dias == 1){
                periodoTexto = "un día";
            } else if (dias == 7) {
                periodoTexto = "una semana";
            } else if (dias == 30 || dias == 31) {
                periodoTexto = "un mes";
            } else {
                periodoTexto = dias + " días";
            }

            String resumen = "Total Habitaciones Reservadas | " + total + " ingresos durante " + periodoTexto;

            model.addAttribute("listaReservas", reservas);
            model.addAttribute("fechaEntrada", fechaEntrada);
            model.addAttribute("fechaSalida", fechaSalida);
            model.addAttribute("totalResumen", resumen);
        }

        return "admin/reporteHabitaciones";
    }

    @GetMapping("/admin/reportesClientes")
    public String reporteClientesFrecuentes(HttpServletRequest request, Model model) {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);

        List<ReporteClientesDto> lista = adminDao.obtenerClientesFrecuentes(inicioMes, hoy);

        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("fechaEntrada", inicioMes);
        model.addAttribute("fechaSalida", hoy);
        model.addAttribute("listaClientes", lista);
        return "admin/reporteClientes";
    }

    @PostMapping("/admin/reportesClientes")
    public String procesarReporteClientes(
            @RequestParam("fechaEntrada") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaEntrada,
            @RequestParam("fechaSalida") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaSalida,
            Model model,
            HttpServletRequest request
    ) {
        model.addAttribute("currentPath", request.getRequestURI());

        if (fechaEntrada != null && fechaSalida != null) {
            List<ReporteClientesDto> lista = adminDao.obtenerClientesFrecuentes(fechaEntrada, fechaSalida);

            model.addAttribute("listaClientes", lista);
            model.addAttribute("fechaEntrada", fechaEntrada);
            model.addAttribute("fechaSalida", fechaSalida);
        }

        return "admin/reporteClientes";
    }

    @GetMapping("/admin/reportesHabitacionesFrecuentes")
    public String reporteHabitacionesFrecuentes(HttpServletRequest request, Model model) {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);

        List<ReporteHabFrecuenteDto> lista = adminDao.obtenerHabitacionesFrecuentes(inicioMes, hoy);

        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("fechaEntrada", inicioMes);
        model.addAttribute("fechaSalida", hoy);
        model.addAttribute("listaHabitaciones", lista);
        return "admin/reporteHabitacionesFrecuentes";
    }

    @PostMapping("/admin/reportesHabitacionesFrecuentes")
    public String procesarReporteHabitacionesFrecuentes(
            @RequestParam("fechaEntrada") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaEntrada,
            @RequestParam("fechaSalida") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaSalida,
            Model model,
            HttpServletRequest request
    ) {
        model.addAttribute("currentPath", request.getRequestURI());

        if (fechaEntrada != null && fechaSalida != null) {
            List<ReporteHabFrecuenteDto> lista = adminDao.obtenerHabitacionesFrecuentes(fechaEntrada, fechaSalida);

            model.addAttribute("listaHabitaciones", lista);
            model.addAttribute("fechaEntrada", fechaEntrada);
            model.addAttribute("fechaSalida", fechaSalida);
        }

        return "admin/reporteHabitacionesFrecuentes";
    }

    @GetMapping("/admin/reporteOcupabilidad")
    public String reporteOcupabilidad(HttpServletRequest request, Model model) {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);

        List<ReporteOcupabilidadPisoDto> lista = adminDao.obtenerOcupabilidadPorPisos(inicioMes, hoy);

        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("fechaEntrada", inicioMes);
        model.addAttribute("fechaSalida", hoy);
        model.addAttribute("listaOcupabilidad", lista);
        return "admin/reporteOcupabilidad";
    }

    @PostMapping("/admin/reporteOcupabilidad")
    public String procesarReporteOcupabilidad(
            @RequestParam("fechaEntrada") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaEntrada,
            @RequestParam("fechaSalida") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaSalida,
            Model model,
            HttpServletRequest request
    ) {
        model.addAttribute("currentPath", request.getRequestURI());

        if (fechaEntrada != null && fechaSalida != null) {
            List<ReporteOcupabilidadPisoDto> lista = adminDao.obtenerOcupabilidadPorPisos(fechaEntrada, fechaSalida);

            model.addAttribute("listaOcupabilidad", lista);
            model.addAttribute("fechaEntrada", fechaEntrada);
            model.addAttribute("fechaSalida", fechaSalida);
        }

        return "admin/reporteOcupabilidad";
    }

    @GetMapping("/admin/exportar-pdf-habitaciones")
    public void exportarReportePDF(
            @RequestParam("fechaEntrada") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaEntrada,
            @RequestParam("fechaSalida") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaSalida,
            HttpServletResponse response
    ) throws IOException, DocumentException {

        List<ReporteReservasDto> reservas = adminDao.obtenerReporteReservasConfirmadas(fechaEntrada, fechaSalida);
        long dias = ChronoUnit.DAYS.between(fechaEntrada, fechaSalida) + 1;
        LocalDate hoy = LocalDate.now();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_habitaciones_confirmadas.pdf");

        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Font fontSub = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Font fontCabecera = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        Font fontCelda = FontFactory.getFont(FontFactory.HELVETICA, 9);

        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        header.setWidths(new int[]{1, 1});

        PdfPCell celdaIzq = new PdfPCell(new Phrase("Hospedaje", fontSub));
        celdaIzq.setBorder(Rectangle.NO_BORDER);
        celdaIzq.setHorizontalAlignment(Element.ALIGN_LEFT);

        PdfPCell celdaDer = new PdfPCell(new Phrase("Fecha de generación: " + hoy, fontSub));
        celdaDer.setBorder(Rectangle.NO_BORDER);
        celdaDer.setHorizontalAlignment(Element.ALIGN_RIGHT);

        header.addCell(celdaIzq);
        header.addCell(celdaDer);
        document.add(header);

        Paragraph titulo = new Paragraph("Reporte de Ingresos del " + fechaEntrada + " al " + fechaSalida + " (" + dias + " días)", fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingBefore(10);
        titulo.setSpacingAfter(10);
        document.add(titulo);

        PdfPTable table = new PdfPTable(9);
        table.setWidthPercentage(100);
        table.setSpacingBefore(5);
        table.setWidths(new float[]{2.5f, 1.5f, 3f, 2f, 2.5f, 2.5f, 2.5f, 2.5f, 2f});

        Stream.of("Código", "Hab", "Cliente", "DNI", "F. Entrada", "F. Salida", "H. Entrada", "H. Salida", "Total")
                .forEach(headerTitle -> {
                    PdfPCell headerCell = new PdfPCell(new Phrase(headerTitle, fontCabecera));
                    headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    table.addCell(headerCell);
                });

        for (ReporteReservasDto r : reservas) {
            table.addCell(new PdfPCell(new Phrase(r.getCodigoReserva(), fontCelda)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(r.getNumHabitacion()), fontCelda)));
            table.addCell(new PdfPCell(new Phrase(r.getNombreCliente() + " " + r.getApellidoCliente(), fontCelda)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(r.getDni()), fontCelda)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(r.getFechaEntrada()), fontCelda)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(r.getFechaSalida()), fontCelda)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(r.getHoraEntrada()), fontCelda)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(r.getHoraSalida()), fontCelda)));
            table.addCell(new PdfPCell(new Phrase("S/ " + r.getTotal(), fontCelda)));
        }

        document.add(table);
        document.close();
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
