package com.example.serviciousuario.repository;

import org.springframework.data.jpa.repository.JpaRepository; // Importa JpaRepository
import org.springframework.stereotype.Repository;

import com.example.serviciousuario.entity.Rol;
import com.example.serviciousuario.entity.Usuario;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsuarioAndClave(String usuario, String clave);
    Optional<Usuario> findByUsuarioAndEstado(String usuario, Boolean estado);
    Optional<Usuario> findByUsuario(String usuario);
    List<Usuario> findByRol(Rol rol); // Buscar usuarios por rol
}
