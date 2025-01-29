package com.example.serviciousuario.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "rol")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre; // Por ejemplo, "ROLE_ADMIN", "ROLE_USER"

    @Column(nullable = false)
    private String descripcion; // Una descripción del rol

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime fechaCreacion;
}
