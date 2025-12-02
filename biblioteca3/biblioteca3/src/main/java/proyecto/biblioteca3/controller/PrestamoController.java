package proyecto.biblioteca3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import proyecto.biblioteca3.repository.PrestamoRepository;
import proyecto.biblioteca3.dto.*;
import proyecto.biblioteca3.model.*;
import proyecto.biblioteca3.service.*;
import proyecto.biblioteca3.command.*;
import proyecto.biblioteca3.validador.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/api/prestamos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PrestamoController {

        @Autowired
        private PrestamoRepository prestamoRepository;

        private final PrestamoService prestamoService;
        private final PrestamoCommand prestamoCommand;
        private final LibroService libroService;
        private final ProxyService proxyService;
        private final DevolucionCommand devolucionCommand;
        
        // ✅ Validadores modulares
        private final ValidadorPrestamos validadorPrestamos;  // TU VALIDADOR ORIGINAL
        private final ValidacionUsuario validadorUsuarios;
        private final ValidacionLibro validadorLibros;
        

        @GetMapping
        public ResponseEntity<ApiResponse<List<Prestamo>>> listar(@RequestParam Integer usuarioId) {
                Usuario usuario = validadorUsuarios.validarExiste(usuarioId);

                List<Prestamo> prestamos;
                if (proxyService.puedeVerTodosPrestamos(usuario)) {
                        prestamos = prestamoService.obtenerTodos();
                } else {
                        prestamos = prestamoRepository.findByUsuarioId(usuarioId);
                }

                return ResponseEntity.ok(ApiResponse.<List<Prestamo>>builder()
                                .exito(true)
                                .datos(prestamos)
                                .build());
        }

        @PostMapping
        public ResponseEntity<ApiResponse<Prestamo>> crear(@RequestBody PrestamoRequest req) {
                try {
                        // ✅ Validaciones con tus validadores
                        Usuario usuario = validadorUsuarios.validarExiste(req.getUsuarioId());
                        validadorUsuarios.validarActivo(usuario);
                        
                        Libro libro = validadorLibros.validarExiste(req.getLibroId(), "crear préstamo");
                        validadorLibros.validarDisponible(libro);
                        
                        // ✅ Usar TU VALIDADOR ORIGINAL
                        validadorPrestamos.validarPrestamoUnico(req.getUsuarioId(), req.getLibroId());
                        validadorPrestamos.validarLimitePrestamos(req.getUsuarioId(), 3);

                        if (!libroService.prestarLibro(libro.getId())) {
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                                .body(ApiResponse.<Prestamo>builder()
                                                                .exito(false)
                                                                .mensaje("Error al actualizar disponibilidad del libro")
                                                                .build());
                        }

                        Prestamo p = Prestamo.builder()
                                        .usuario(usuario)
                                        .libro(libro)
                                        .build();

                        Prestamo result = prestamoCommand.ejecutar(p);

                        return ResponseEntity.status(HttpStatus.CREATED)
                                        .body(ApiResponse.<Prestamo>builder()
                                                        .exito(true)
                                                        .mensaje("Préstamo creado exitosamente")
                                                        .datos(result)
                                                        .build());
                } catch (IllegalStateException | IllegalArgumentException e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(ApiResponse.<Prestamo>builder()
                                                        .exito(false)
                                                        .mensaje(e.getMessage())
                                                        .build());
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponse.<Prestamo>builder()
                                                        .exito(false)
                                                        .mensaje("Error interno: " + e.getMessage())
                                                        .build());
                }
        }

        @PutMapping("/{id}/devolver")
        public ResponseEntity<ApiResponse<Prestamo>> devolver(
                        @PathVariable Integer id,
                        @RequestParam Integer usuarioId) {
                try {
                        // ✅ Validaciones modulares
                        
                        Prestamo prestamo = validadorPrestamos.validarExiste(id);
                        
                        // ✅ Usar TU VALIDADOR ORIGINAL
                        validadorPrestamos.validarDevolucion(prestamo);
                        

                        Prestamo actualizado = devolucionCommand.ejecutar(id);

                        return ResponseEntity.ok(ApiResponse.<Prestamo>builder()
                                        .exito(true)
                                        .mensaje("Libro devuelto exitosamente")
                                        .datos(actualizado)
                                        .build());
                } catch (IllegalStateException | IllegalArgumentException e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(ApiResponse.<Prestamo>builder()
                                                        .exito(false)
                                                        .mensaje(e.getMessage())
                                                        .build());
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponse.<Prestamo>builder()
                                                        .exito(false)
                                                        .mensaje("Error interno: " + e.getMessage())
                                                        .build());
                }
        }

        @PutMapping("/{id}")
        public ResponseEntity<ApiResponse<Prestamo>> actualizar(
                        @PathVariable Integer id,
                        @RequestBody Prestamo prestamo) {
                try {
                        validadorPrestamos.validarExiste(id);
                        validadorPrestamos.validarCompleto(prestamo);
                        
                        prestamo.setId(id);
                        Prestamo actualizado = prestamoService.actualizar(prestamo);
                        
                        return ResponseEntity.ok(ApiResponse.<Prestamo>builder()
                                        .exito(true)
                                        .datos(actualizado)
                                        .build());
                } catch (IllegalArgumentException e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(ApiResponse.<Prestamo>builder()
                                                        .exito(false)
                                                        .mensaje(e.getMessage())
                                                        .build());
                }
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer id) {
                try {
                        validadorPrestamos.validarExiste(id);
                        prestamoService.eliminar(id);
                        
                        return ResponseEntity.ok(ApiResponse.<Void>builder()
                                        .exito(true)
                                        .mensaje("Préstamo eliminado")
                                        .build());
                } catch (IllegalArgumentException e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(ApiResponse.<Void>builder()
                                                        .exito(false)
                                                        .mensaje(e.getMessage())
                                                        .build());
                }
        }

        @GetMapping("/usuario/{usuarioId}")
        public ResponseEntity<ApiResponse<List<Prestamo>>> obtenerPorUsuario(@PathVariable Integer usuarioId) {
                try {
                        validadorUsuarios.validarExiste(usuarioId);
                        List<Prestamo> prestamos = prestamoRepository.findByUsuarioId(usuarioId);
                        
                        return ResponseEntity.ok(ApiResponse.<List<Prestamo>>builder()
                                        .exito(true)
                                        .datos(prestamos)
                                        .build());
                } catch (IllegalArgumentException e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(ApiResponse.<List<Prestamo>>builder()
                                                        .exito(false)
                                                        .mensaje(e.getMessage())
                                                        .build());
                }
        }
}