package proyecto.biblioteca3.validador;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Validaciones básicas y genéricas del sistema
 */
@Component
public class ValidacionBase {
    
    /**
     * Valida que un campo de texto no esté vacío
     */
    public void campoNoVacio(String valor, String nombreCampo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "El campo '" + nombreCampo + "' no puede estar vacío"
            );
        }
    }
    
    /**
     * Valida que un valor numérico sea positivo
     */
    public void validarPositivo(Integer valor, String nombreCampo) {
        if (valor == null || valor <= 0) {
            throw new IllegalArgumentException(
                "El campo '" + nombreCampo + "' debe ser mayor a 0"
            );
        }
    }
    
    /**
     * Valida que un objeto no sea nulo
     */
    public void validarNoNulo(Object objeto, String nombreObjeto) {
        if (objeto == null) {
            throw new IllegalArgumentException(
                nombreObjeto + " no puede ser nulo"
            );
        }
    }
    
    /**
     * Valida que una lista no esté vacía
     */
    public void validarListaNoVacia(List<?> lista, String nombreLista) {
        if (lista == null || lista.isEmpty()) {
            throw new IllegalArgumentException(
                nombreLista + " no puede estar vacía"
            );
        }
    }
    
    /**
     * Valida que el email tenga formato correcto
     */
    public void validarFormatoEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("El formato del email no es válido");
        }
    }
}
