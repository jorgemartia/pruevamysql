package proyecto.biblioteca3.dto;

import lombok.Data;

@Data
public class PrestamoRequest {
    private Integer usuarioId;
    private Integer libroId;
}
