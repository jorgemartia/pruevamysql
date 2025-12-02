package proyecto.biblioteca3.repository;

import proyecto.biblioteca3.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Integer> {
    List<Prestamo> findByUsuarioId(Integer usuarioId);
    List<Prestamo> findByLibroId(Integer libroId);
    List<Prestamo> findByEstado(Prestamo.EstadoPrestamo estado);
}