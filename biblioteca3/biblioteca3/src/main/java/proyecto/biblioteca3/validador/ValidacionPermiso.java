package proyecto.biblioteca3.validador;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import proyecto.biblioteca3.model.Usuario;
import proyecto.biblioteca3.model.Prestamo;

/**
 * Validaciones de permisos y roles del sistema
 */
@Component
@RequiredArgsConstructor
public class ValidacionPermiso {
    
    private final ValidacionBase validacionBase;
    
    /**
     * Valida que el usuario sea administrador
     */
    public void validarEsAdmin(Usuario usuario) {
        validacionBase.validarNoNulo(usuario, "El usuario");
        
        if (usuario.getRol() != Usuario.RolUsuario.ADMIN) {
            throw new IllegalStateException(
                "No tienes permisos de administrador para esta operación"
            );
        }
    }
    
    /**
     * Valida que el usuario pueda gestionar libros
     */
    public void validarGestionLibros(Usuario usuario) {
        validacionBase.validarNoNulo(usuario, "El usuario");
        
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new IllegalStateException("El usuario no está activo");
        }
        
        validarEsAdmin(usuario);
    }
    
    /**
     * Valida que el usuario pueda ver todos los préstamos
     */
    public void validarVerTodosPrestamos(Usuario usuario) {
        validacionBase.validarNoNulo(usuario, "El usuario");
        
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new IllegalStateException("El usuario no está activo");
        }
        
        validarEsAdmin(usuario);
    }
    
    /**
     * Valida permisos para devolver un préstamo
     */
    public void validarDevolucion(Usuario usuario, Prestamo prestamo) {
        validacionBase.validarNoNulo(usuario, "El usuario");
        validacionBase.validarNoNulo(prestamo, "El préstamo");
        
        boolean esAdmin = usuario.getRol() == Usuario.RolUsuario.ADMIN;
        boolean esPropietario = prestamo.getUsuario().getId().equals(usuario.getId());
        
        if (!esAdmin && !esPropietario) {
            throw new IllegalStateException(
                "No tienes permiso para devolver este préstamo"
            );
        }
    }
}