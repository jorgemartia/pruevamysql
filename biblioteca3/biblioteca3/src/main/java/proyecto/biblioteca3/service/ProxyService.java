package proyecto.biblioteca3.service;

import org.springframework.stereotype.Service;
import proyecto.biblioteca3.model.Usuario;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProxyService {
    
    // Verifica si el usuario tiene permiso de administrador
    public boolean esAdministrador(Usuario usuario) {
        return usuario != null && 
               usuario.getRol() == Usuario.RolUsuario.ADMIN && 
               Boolean.TRUE.equals(usuario.getActivo());
    }
    
    // Verifica si el usuario puede devolver un préstamo específico
    public boolean puedeDevolver(Usuario usuario, Integer prestamoId) {
        if (esAdministrador(usuario)) {
            return true;
        }
        
        // Usuario normal solo puede devolver sus propios préstamos activos
        return usuario != null && Boolean.TRUE.equals(usuario.getActivo());
    }
    
    // Verifica permisos para operaciones con libros
    public boolean puedeGestionarLibros(Usuario usuario) {
        return esAdministrador(usuario);
    }
    
    // Verifica permisos para ver todos los préstamos
    public boolean puedeVerTodosPrestamos(Usuario usuario) {
        return esAdministrador(usuario);
    }
    
    // Verifica si puede gestionar usuarios (solo admin)
    public boolean puedeGestionarUsuarios(Usuario usuario) {
        return esAdministrador(usuario);
    }
}