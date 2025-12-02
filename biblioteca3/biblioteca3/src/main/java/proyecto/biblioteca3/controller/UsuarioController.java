package proyecto.biblioteca3.controller;

import proyecto.biblioteca3.dto.*;
import proyecto.biblioteca3.model.*;
import proyecto.biblioteca3.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;


    @PostMapping("/registro")
    @SuppressWarnings("CallToPrintStackTrace")
    public ResponseEntity<ApiResponse<Usuario>> registrar(@RequestBody RegistroRequest request) {
        try {
            System.out.println("=== DEBUG REGISTRO ===");
            System.out.println("Nombre: " + request.getNombre());
            System.out.println("Email: " + request.getEmail());
            System.out.println("Clave: " + (request.getClave() != null ? "OK" : "NULL"));

            Usuario creado = usuarioService.registrarUsuario(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<Usuario>builder()
                            .exito(true)
                            .mensaje("Usuario registrado exitosamente")
                            .datos(creado)
                            .build());
        } catch (Exception e) {
            System.err.println("ERROR EN REGISTRO: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<Usuario>builder()
                            .exito(false)
                            .mensaje(e.getMessage())
                            .error(e.getClass().getSimpleName())
                            .build());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Usuario>> login(@RequestBody LoginRequest req) {
        return usuarioService.autenticar(req.getEmail(), req.getClave())
                .map(u -> ResponseEntity.ok(ApiResponse.<Usuario>builder().exito(true).datos(u).build()))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.<Usuario>builder().exito(false).mensaje("Credenciales inválidas").build()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Usuario>>> listar() {
        List<Usuario> usuarios = usuarioService.obtenerTodos();
        return ResponseEntity.ok(ApiResponse.<List<Usuario>>builder().exito(true).datos(usuarios).build());
    }
}