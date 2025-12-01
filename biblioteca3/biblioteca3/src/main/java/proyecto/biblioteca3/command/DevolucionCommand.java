package proyecto.biblioteca3.command;

import proyecto.biblioteca3.model.Prestamo;
import proyecto.biblioteca3.service.LibroService;
import proyecto.biblioteca3.service.PrestamoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Comando para devolver un libro prestado.
 * Encapsula la lógica de devolución siguiendo el patrón Command.
 */
@Component
@RequiredArgsConstructor
public class DevolucionCommand implements Command<Prestamo> {

    private final PrestamoService prestamoService;
    private final LibroService libroService;
    private Integer prestamoId;

    /**
     * Configura el ID del préstamo a devolver.
     */
    public DevolucionCommand configurar(Integer prestamoId) {
        this.prestamoId = prestamoId;
        return this;
    }

    /**
     * Ejecuta la devolución del libro:
     * - Busca el préstamo
     * - Actualiza su estado a DEVUELTO
     * - Incrementa la disponibilidad del libro
     * 
     * @return El préstamo actualizado
     */
    @Override
    public Prestamo ejecutar() {
        if (prestamoId == null) {
            throw new IllegalStateException("Debe configurar el ID del préstamo");
        }

        Prestamo prestamo = prestamoService.obtenerPorId(prestamoId);

        if (prestamo == null) {
            throw new IllegalArgumentException("Préstamo no encontrado");
        }

        if (prestamo.getEstado() == Prestamo.EstadoPrestamo.DEVUELTO) {
            throw new IllegalStateException("Este préstamo ya fue devuelto");
        }

        // Actualizar el préstamo
        prestamo.setFechaDevolucionReal(LocalDate.now());
        prestamo.setEstado(Prestamo.EstadoPrestamo.DEVUELTO);

        // Incrementar disponibilidad del libro
        libroService.devolverLibro(prestamo.getLibro().getId());

        return prestamoService.actualizar(prestamo);
    }

    /**
     * Método de conveniencia para ejecutar directamente.
     */
    public Prestamo ejecutar(Integer prestamoId) {
        this.prestamoId = prestamoId;
        return ejecutar();
    }
}