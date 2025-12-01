package proyecto.biblioteca3.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ApiResponse<T> {
    private boolean exito;
    private String mensaje;
    private T datos;
    private String error;
}
