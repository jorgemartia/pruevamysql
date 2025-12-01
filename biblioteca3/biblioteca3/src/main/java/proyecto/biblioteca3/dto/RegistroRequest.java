package proyecto.biblioteca3.dto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class RegistroRequest {
    private String nombre;
    private String apellido;
    private String cedula;
    private String telefono;
    private String email;
    private String clave;
}

