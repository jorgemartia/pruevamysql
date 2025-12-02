package proyecto.biblioteca3.validador;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import proyecto.biblioteca3.model.Prestamo;
import proyecto.biblioteca3.repository.PrestamoRepository;

import java.util.List;

/**
 * Validador de reglas de negocio para préstamos
 */
@Component
@RequiredArgsConstructor
public class ValidadorPrestamos {
    
    private final PrestamoRepository prestamoRepository;
    
    /**
     * Valida si un usuario puede solicitar un préstamo de un libro específico
     * 
     * @param usuarioId ID del usuario
     * @param libroId ID del libro
     * @throws IllegalStateException si el usuario ya tiene un préstamo activo del mismo libro
     */
    public void validarPrestamoUnico(Integer usuarioId, Integer libroId) {
        List<Prestamo> prestamosUsuario = prestamoRepository.findByUsuarioId(usuarioId);
        
        boolean tienePrestamoActivo = prestamosUsuario.stream()
            .anyMatch(prestamo -> 
                prestamo.getLibro().getId().equals(libroId) && 
                prestamo.getEstado() == Prestamo.EstadoPrestamo.ACTIVO
            );
        
        if (tienePrestamoActivo) {
            throw new IllegalStateException(
                "Ya tienes un préstamo activo de este libro. Devuélvelo antes de solicitar otro."
            );
        }
    }
    
    /**
     * Valida si un usuario puede solicitar más préstamos (límite de préstamos activos)
     * 
     * @param usuarioId ID del usuario
     * @param limiteMaximo Número máximo de préstamos activos permitidos
     * @throws IllegalStateException si el usuario excede el límite de préstamos activos
     */
    public void validarLimitePrestamos(Integer usuarioId, int limiteMaximo) {
        List<Prestamo> prestamosUsuario = prestamoRepository.findByUsuarioId(usuarioId);
        
        long prestamosActivos = prestamosUsuario.stream()
            .filter(prestamo -> prestamo.getEstado() == Prestamo.EstadoPrestamo.ACTIVO)
            .count();
        
        if (prestamosActivos >= limiteMaximo) {
            throw new IllegalStateException(
                String.format("Has alcanzado el límite de %d préstamos activos. Devuelve algún libro para poder solicitar otro.", limiteMaximo)
            );
        }
    }
    
    /**
     * Valida si un préstamo puede ser devuelto
     * 
     * @param prestamo El préstamo a validar
     * @throws IllegalStateException si el préstamo ya fue devuelto
     */
    public void validarDevolucion(Prestamo prestamo) {
        if (prestamo.getEstado() == Prestamo.EstadoPrestamo.DEVUELTO) {
            throw new IllegalStateException("Este préstamo ya fue devuelto anteriormente");
        }
    }
}