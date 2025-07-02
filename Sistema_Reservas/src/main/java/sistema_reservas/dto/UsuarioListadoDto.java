package sistema_reservas.dto;

import lombok.Data;

@Data
public class UsuarioListadoDto {

    private Integer idusuario;
    private String nombre;
    private String apellido;
    private Integer dni;
    private String correo;
    private Integer rolId;
    private String rolNombre;
}
