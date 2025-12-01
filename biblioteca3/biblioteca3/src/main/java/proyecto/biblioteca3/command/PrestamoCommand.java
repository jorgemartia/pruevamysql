package proyecto.biblioteca3.command;

import proyecto.biblioteca3.model.*;
import proyecto.biblioteca3.service.PrestamoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Comando que ejecuta el préstamo de un libro a un usuario.
 * Implementa el patrón Command para encapsular la lógica de préstamo.
 */
@Component
@RequiredArgsConstructor
public class PrestamoCommand implements Command<Prestamo> {

    private final PrestamoService prestamoService;
    private Prestamo prestamo;

    /**
     * Configura el préstamo antes de ejecutar.
     * 
     * @param prestamo El préstamo a procesar
     * @return Esta instancia del comando para permitir fluent API
     */
    public PrestamoCommand conPrestamo(Prestamo prestamo) {
        this.prestamo = prestamo;
        return this;
    }

    /**
     * Ejecuta el comando de préstamo:
     * - Establece la fecha de préstamo actual
     * - Calcula la fecha de devolución esperada (14 días)
     * - Establece el estado como ACTIVO
     * - Persiste el préstamo en la base de datos
     * 
     * @return El préstamo guardado
     * @throws IllegalStateException si el préstamo no ha sido configurado
     */
    @Override
    public Prestamo ejecutar() {
        if (prestamo == null) {
            throw new IllegalStateException("Debe configurar el préstamo antes de ejecutar");
        }

        prestamo.setFechaPrestamo(LocalDateTime.now());
        prestamo.setFechaDevolucionEsperada(LocalDate.now().plusDays(14));
        prestamo.setEstado(Prestamo.EstadoPrestamo.ACTIVO);

        return prestamoService.guardar(prestamo);
    }

    /**
     * Método de conveniencia para ejecutar directamente con un préstamo.
     * 
     * @param prestamo El préstamo a procesar
     * @return El préstamo guardado
     */
    public Prestamo ejecutar(Prestamo prestamo) {
        this.prestamo = prestamo;
        return ejecutar();
    }
}