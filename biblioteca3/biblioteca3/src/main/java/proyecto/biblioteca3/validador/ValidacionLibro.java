package proyecto.biblioteca3.validador;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import proyecto.biblioteca3.model.Libro;
import proyecto.biblioteca3.repository.LibroRepository;
import java.util.Optional;

/**
 * Validaciones específicas para la entidad Libro
 */
@Component
@RequiredArgsConstructor
public class ValidacionLibro {
    
    private final LibroRepository libroRepository;
    private final ValidacionBase validacionBase;
    
    /**
     * Valida que el libro tenga todos los campos requeridos
     */
    public void validarCompleto(Libro libro) {
        validacionBase.validarNoNulo(libro, "El libro");
        validacionBase.campoNoVacio(libro.getTitulo(), "Título");
        validacionBase.campoNoVacio(libro.getAutor(), "Autor");
        validacionBase.campoNoVacio(libro.getIsbn(), "ISBN");
        validacionBase.validarPositivo(libro.getCantidadTotal(), "Cantidad total");
    }
    
    /**
     * Valida que el libro no exista ya en el catálogo (por ISBN)
     */
    public void validarNoExiste(String isbn) {
        validacionBase.campoNoVacio(isbn, "ISBN");
        
        Optional<Libro> libroExistente = libroRepository.findAll().stream()
            .filter(l -> l.getIsbn().equals(isbn))
            .findFirst();
            
        if (libroExistente.isPresent()) {
            throw new IllegalStateException(
                "El libro con ISBN '" + isbn + "' ya existe en el catálogo"
            );
        }
    }
    
    /**
     * Valida que el libro exista en el sistema
     */
    public Libro validarExiste(Integer libroId, String contexto) {
        return libroRepository.findById(libroId)
            .orElseThrow(() -> new IllegalArgumentException(
                "No se encontró el libro en el catálogo" + 
                (contexto != null ? ": " + contexto : "")
            ));
    }
    
    /**
     * Valida que el libro esté disponible para préstamo
     */
    public void validarDisponible(Libro libro) {
        validacionBase.validarNoNulo(libro, "El libro");
        
        if (libro.getCantidadDisponible() == null || libro.getCantidadDisponible() <= 0) {
            throw new IllegalStateException(
                "El libro '" + libro.getTitulo() + "' no está disponible"
            );
        }
    }
    
    /**
     * Valida que las cantidades del libro sean coherentes
     */
    public void validarCantidades(Libro libro) {
        validacionBase.validarNoNulo(libro, "El libro");
        
        if (libro.getCantidadDisponible() != null && libro.getCantidadDisponible() < 0) {
            throw new IllegalArgumentException(
                "La cantidad disponible no puede ser negativa"
            );
        }
        
        if (libro.getCantidadDisponible() != null && 
            libro.getCantidadTotal() != null && 
            libro.getCantidadDisponible() > libro.getCantidadTotal()) {
            throw new IllegalArgumentException(
                "La cantidad disponible no puede ser mayor a la cantidad total"
            );
        }
    }
}
