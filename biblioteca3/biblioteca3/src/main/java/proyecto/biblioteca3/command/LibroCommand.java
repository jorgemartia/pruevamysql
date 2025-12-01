package proyecto.biblioteca3.command;

import proyecto.biblioteca3.model.Libro;
import proyecto.biblioteca3.service.LibroService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Comando para agregar o actualizar libros en el sistema.
 * Implementa el patrón Command para encapsular la lógica de gestión de libros.
 */
@Component
@RequiredArgsConstructor
public class LibroCommand implements Command<Libro> {

    private final LibroService libroService;
    private Libro libro;
    private TipoOperacion operacion;

    public enum TipoOperacion {
        AGREGAR, ACTUALIZAR
    }

    /**
     * Configura el libro y la operación a ejecutar.
     * 
     * @param libro     El libro a procesar
     * @param operacion El tipo de operación (AGREGAR o ACTUALIZAR)
     * @return Esta instancia del comando
     */
    public LibroCommand configurar(Libro libro, TipoOperacion operacion) {
        this.libro = libro;
        this.operacion = operacion;
        return this;
    }

    /**
     * Ejecuta el comando según la operación configurada.
     * 
     * @return El libro guardado o actualizado
     */
    @Override
    public Libro ejecutar() {
        if (libro == null || operacion == null) {
            throw new IllegalStateException("Debe configurar el libro y la operación");
        }

        return switch (operacion) {
            case AGREGAR -> agregarLibro();
            case ACTUALIZAR -> actualizarLibro();
        };
    }

    /**
     * Agrega un nuevo libro al sistema.
     */
    private Libro agregarLibro() {
        validarCampos();

        if (libro.getId() == null) {
            libro.setCantidadDisponible(libro.getCantidadTotal());
            libro.setFechaIngreso(LocalDateTime.now());
        }

        return libroService.guardar(libro);
    }

    /**
     * Actualiza un libro existente.
     */
    private Libro actualizarLibro() {
        validarCampos();
        return libroService.guardar(libro);
    }

    /**
     * Valida que los campos requeridos del libro estén completos.
     */
    private void validarCampos() {
        if (libro.getTitulo() == null || libro.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("El título del libro es requerido");
        }
        if (libro.getAutor() == null || libro.getAutor().trim().isEmpty()) {
            throw new IllegalArgumentException("El autor del libro es requerido");
        }
        if (libro.getCantidadTotal() == null || libro.getCantidadTotal() <= 0) {
            throw new IllegalArgumentException("La cantidad total debe ser mayor a 0");
        }
    }
}