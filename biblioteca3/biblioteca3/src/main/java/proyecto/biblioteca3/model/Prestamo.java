package proyecto.biblioteca3.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "prestamos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prestamo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "libro_id", nullable = false)
    private Libro libro;

    @Column(nullable = false)
    private LocalDateTime fechaPrestamo;

    @Column(nullable = false)
    private LocalDate fechaDevolucionEsperada;

    @Column
    private LocalDate fechaDevolucionReal;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoPrestamo estado;

    public enum EstadoPrestamo {
        ACTIVO, DEVUELTO, VENCIDO
    }

    @PrePersist
    protected void onCreate() {
        if (fechaPrestamo == null) fechaPrestamo = LocalDateTime.now();
        if (estado == null) estado = EstadoPrestamo.ACTIVO;
    }
}
