package proyecto.biblioteca3.validador;


import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import proyecto.biblioteca3.model.Usuario;
import proyecto.biblioteca3.repository.UsuarioRepository;
import java.util.Optional;

/**
 * Validaciones específicas para la entidad Usuario
 */
@Component
@RequiredArgsConstructor
public class ValidacionUsuario {
    
    private final UsuarioRepository usuarioRepository;
    private final ValidacionBase validacionBase;
    
    /**
     * Valida que el usuario tenga todos los campos requeridos
     */
    public void validarCompleto(Usuario usuario) {
        validacionBase.validarNoNulo(usuario, "El usuario");
        validacionBase.campoNoVacio(usuario.getNombre(), "Nombre");
        validacionBase.campoNoVacio(usuario.getApellido(), "Apellido");
        validacionBase.campoNoVacio(usuario.getCedula(), "Cédula");
        validacionBase.campoNoVacio(usuario.getEmail(), "Email");
        validacionBase.campoNoVacio(usuario.getClave(), "Contraseña");
        
        validacionBase.validarFormatoEmail(usuario.getEmail());
        validarLongitudClave(usuario.getClave());
    }
    
    /**
     * Valida que la contraseña tenga longitud mínima
     */
    public void validarLongitudClave(String clave) {
        validacionBase.campoNoVacio(clave, "Contraseña");
        
        if (clave.length() < 6) {
            throw new IllegalArgumentException(
                "La contraseña debe tener al menos 6 caracteres"
            );
        }
    }
    
    /**
     * Valida que el email no esté duplicado
     */
    public void validarEmailUnico(String email, Integer usuarioId) {
        validacionBase.campoNoVacio(email, "Email");
        
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(email);
        
        if (usuarioExistente.isPresent()) {
            if (usuarioId == null || !usuarioExistente.get().getId().equals(usuarioId)) {
                throw new IllegalStateException(
                    "El email '" + email + "' ya está registrado"
                );
            }
        }
    }
    
    /**
     * Valida que la cédula no esté duplicada
     */
    public void validarCedulaUnica(String cedula, Integer usuarioId) {
        validacionBase.campoNoVacio(cedula, "Cédula");
        
        Optional<Usuario> usuarioExistente = usuarioRepository.findByCedula(cedula);
        
        if (usuarioExistente.isPresent()) {
            if (usuarioId == null || !usuarioExistente.get().getId().equals(usuarioId)) {
                throw new IllegalStateException(
                    "La cédula '" + cedula + "' ya está registrada"
                );
            }
        }
    }
    
    /**
     * Valida que el usuario exista en el sistema
     */
    public Usuario validarExiste(Integer usuarioId) {
        return usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }
    
    /**
     * Valida que el usuario esté activo
     */
    public void validarActivo(Usuario usuario) {
        validacionBase.validarNoNulo(usuario, "El usuario");
        
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new IllegalStateException(
                "El usuario no está activo en el sistema"
            );
        }
    }
}
