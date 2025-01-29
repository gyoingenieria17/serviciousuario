package com.example.serviciousuario.entity;

import java.time.LocalDateTime; // Importación correcta
import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String email;
    private String telefono;

    @Column(name = "usuario", unique = true, nullable = false)
    private String usuario;

    private String clave;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    private Boolean estado;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();

        if (this.rol == null) {
            throw new IllegalArgumentException("El rol del usuario no puede ser nulo.");
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}
