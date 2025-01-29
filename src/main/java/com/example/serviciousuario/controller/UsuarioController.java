package com.example.serviciousuario.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.serviciousuario.service.UsuarioService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.example.serviciousuario.entity.Usuario;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping(path = "api/v1/usuario")
public class UsuarioController {

    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario>> getAll() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new IllegalArgumentException("No tienes permiso para acceder a este recurso.");
        }
        List<Usuario> usuarios = usuarioService.getUsuario();
        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build(); // Código 204 si no hay usuarios
        }
        return ResponseEntity.ok(usuarios); // Código 200 con la lista de usuarios
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario usuarioRequest) {
        if (usuarioRequest.getUsuario() == null || usuarioRequest.getClave() == null) {
            return ResponseEntity.badRequest().body("Usuario y clave no pueden estar vacíos"); // Código 400
        }
        try {
            Optional<Usuario> usuarioOpt = usuarioService.getUsuario(usuarioRequest.getUsuario(), usuarioRequest.getClave());
            return ResponseEntity.ok(usuarioOpt.get());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/insertar")
    public ResponseEntity<?> insertarUsuario(@RequestBody Usuario usuario) {
        try {
            Usuario usuarioInsertado = usuarioService.insertarUsuario(usuario);
            return ResponseEntity.ok(usuarioInsertado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<?> borrarUsuario(@RequestBody Map<String, Long> request) {
        Long id = request.get("id");
        try {
            usuarioService.borrarUsuario(id);
            return ResponseEntity.ok("Usuario con id " + id + " fue desactivado correctamente.");
        } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuarioActualizado) {
        log.info("Iniciando actualización para el usuario con id: {}", id);
        try {
            Usuario usuario = usuarioService.actualizarUsuario(id, usuarioActualizado);
            log.info("Actualización completada para el usuario con id: {}", id);
            return ResponseEntity.ok(usuario);
        } catch (IllegalArgumentException e) {
            log.error("Error actualizando el usuario con id {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
