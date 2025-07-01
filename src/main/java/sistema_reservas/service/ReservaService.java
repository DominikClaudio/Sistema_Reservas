package sistema_reservas.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import sistema_reservas.model.Reserva;
import sistema_reservas.model.Usuario;
import sistema_reservas.repository.ReservaRepository;
import sistema_reservas.repository.UsuarioRepository;

// 3) Implementa el servicio de reservas
// src/main/java/sistema_reservas/service/ReservaService.java
@Service
public class ReservaService {
    @Autowired
    private ReservaRepository reservaRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    /**
     * Crea y persiste una nueva reserva en la base de datos.
     *
     * @param correoUsuario correo del usuario que hace la reserva
     * @param tipohabId     id del tipo de habitación
     * @param fechaEntrada  fecha de entrada
     * @param fechaSalida   fecha de salida
     * @param pisoId        id del piso
     * @param adultos       número de adultos
     * @param ninos         número de niños
     * @return la entidad Reserva recién guardada
     */
    public Reserva crearReserva(
            String correoUsuario,
            int tipohabId,
            LocalDate fechaEntrada,
            LocalDate fechaSalida,
            int pisoId,
            int adultos,
            int ninos
    ) {
        // 1) Obtener el usuario por correo
        Usuario usuario = usuarioRepo.findByCorreo(correoUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + correoUsuario));

        // 2) Construir la reserva
        Reserva r = new Reserva();
        r.setUsuario(usuario);
        //r.setTipohabId(tipohabId);
        r.setFechaentrada(fechaEntrada);
        r.setFechasalida(fechaSalida);
        //r.setPisoId(pisoId);
        //r.setAdultos(adultos);
        //r.setNinos(ninos);

        // 3) Generar un código único para la reserva
        r.setCodigoreserva(UUID.randomUUID().toString());

        // 4) Asignar estado inicial (por ejemplo, 1 = PENDIENTE)
        r.setEstadoid(1);

        // 5) Registrar hora de entrada/salida (opcional)
        r.setHoraentrada(LocalTime.now());
        r.setHorasalida(LocalTime.of(12, 0));  // por defecto, medio día de checkout

        // 6) Guardar y devolver
        return reservaRepo.save(r);
        
        
    }
    /**
     * Devuelve todas las reservas del usuario identificado por su correo.
     */
    public List<Reserva> listarReservasPorUsuario(String correoUsuario) {
        Usuario u = usuarioRepo.findByCorreo(correoUsuario)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + correoUsuario));
        return reservaRepo.findByUsuario(u);
    }
}
