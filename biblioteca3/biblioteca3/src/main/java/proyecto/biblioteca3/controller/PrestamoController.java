package proyecto.biblioteca3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import proyecto.biblioteca3.repository.PrestamoRepository;
import proyecto.biblioteca3.dto.*;
import proyecto.biblioteca3.validador.*;    
import proyecto.biblioteca3.model.*;
import proyecto.biblioteca3.service.*;
import proyecto.biblioteca3.command.*;
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
        private final UsuarioService usuarioService;
        private final LibroService libroService;
        private final ProxyService proxyService;
        private final DevolucionCommand devolucionCommand;
        private final ValidadorPrestamos validadorPrestamos;

        @GetMapping
        public ResponseEntity<ApiResponse<List<Prestamo>>> listar(@RequestParam Integer usuarioId) {
                Usuario usuario = usuarioService.obtenerPorId(usuarioId)
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

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
                        Usuario usuario = usuarioService.obtenerPorId(req.getUsuarioId())
                                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                        Libro libro = libroService.obtenerPorId(req.getLibroId())
                                        .orElseThrow(() -> new RuntimeException("Libro no encontrado"));

                                                             
                        validadorPrestamos.validarPrestamoUnico(req.getUsuarioId(), req.getLibroId());
                        
                        validadorPrestamos.validarLimitePrestamos(req.getUsuarioId(), 3);

                        if (libro.getCantidadDisponible() <= 0) {
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                                .body(ApiResponse.<Prestamo>builder()
                                                                .exito(false)
                                                                .mensaje("No hay ejemplares disponibles de este libro")
                                                                .build());
                        }

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
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponse.<Prestamo>builder()
                                                        .exito(false)
                                                        .mensaje(e.getMessage())
                                                        .build());
                }
        }

        @PutMapping("/{id}/devolver")
        public ResponseEntity<ApiResponse<Prestamo>> devolver(
                        @PathVariable Integer id,
                        @RequestParam Integer usuarioId) {
                try {
                        Usuario usuario = usuarioService.obtenerPorId(usuarioId)
                                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                        Prestamo prestamo = prestamoService.obtenerPorId(id);
                        if (prestamo == null) {
                                return ResponseEntity.notFound().build();
                        }
                        validadorPrestamos.validarDevolucion(prestamo);
                        // Validar permisos
                        if (!usuario.getRol().equals(Usuario.RolUsuario.ADMIN) &&
                                        !prestamo.getUsuario().getId().equals(usuarioId)) {
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body(ApiResponse.<Prestamo>builder()
                                                                .exito(false)
                                                                .mensaje("No tiene permiso para devolver este préstamo")
                                                                .build());
                        }

                        // ✅ Usar el patrón Command
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
                                                        .mensaje(e.getMessage())
                                                        .build());
                }
        }

        // ✅ CORREGIDO: Cambiar Long por Integer
        @PutMapping("/{id}")
        public ResponseEntity<ApiResponse<Prestamo>> actualizar(
                        @PathVariable Integer id, // ✅ Cambiado de Long a Integer
                        @RequestBody Prestamo prestamo) {
                prestamo.setId(id); // ✅ Eliminar .intValue()
                Prestamo actualizado = prestamoService.actualizar(prestamo);
                return ResponseEntity.ok(ApiResponse.<Prestamo>builder()
                                .exito(true)
                                .datos(actualizado)
                                .build());
        }

        // ✅ CORREGIDO: Cambiar Long por Integer
        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer id) { // ✅ Cambiado de Long a Integer
                prestamoService.eliminar(id); // ✅ Eliminar .intValue()
                return ResponseEntity.ok(ApiResponse.<Void>builder()
                                .exito(true)
                                .mensaje("Préstamo eliminado")
                                .build());
        }

        @GetMapping("/usuario/{usuarioId}")
        public ResponseEntity<ApiResponse<List<Prestamo>>> obtenerPorUsuario(@PathVariable Integer usuarioId) {
                List<Prestamo> prestamos = prestamoRepository.findByUsuarioId(usuarioId);
                return ResponseEntity.ok(ApiResponse.<List<Prestamo>>builder()
                                .exito(true)
                                .datos(prestamos)
                                .build());
        }
}