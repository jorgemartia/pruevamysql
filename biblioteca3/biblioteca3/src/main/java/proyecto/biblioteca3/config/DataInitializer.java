package proyecto.biblioteca3.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import proyecto.biblioteca3.model.Usuario;
import proyecto.biblioteca3.repository.UsuarioRepository;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {

            // Crear usuario administrador por defecto si no existe
            if (usuarioRepository.findByEmail("admin@biblioteca.com").isEmpty()) {
                Usuario admin = Usuario.builder()
                        .nombre("Administrador")
                        .apellido("Sistema")
                        .cedula("0000000000")
                        .telefono("0000000000")
                        .email("admin@biblioteca.com")
                        .clave(passwordEncoder.encode("admin123"))
                        .rol(Usuario.RolUsuario.ADMIN)
                        .activo(true)
                        .fechaRegistro(LocalDateTime.now())
                        .build();
                usuarioRepository.save(admin);
            }
        };
    }
}
