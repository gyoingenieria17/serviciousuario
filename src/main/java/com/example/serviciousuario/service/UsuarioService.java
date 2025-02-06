package com.example.serviciousuario.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.serviciousuario.dto.AuthResponse;
import com.example.serviciousuario.entity.Rol;
import com.example.serviciousuario.entity.Usuario;
import com.example.serviciousuario.repository.RolRepository;
import com.example.serviciousuario.repository.UsuarioRepository;
import com.example.serviciousuario.util.JwtUtil;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    UsuarioRepository usuarioRepository;
    
    @Autowired
    RolRepository rolRepository;

    @Autowired
    JwtUtil jwtUtil;

    public List<Usuario> getUsuario() {
        return usuarioRepository.findAll();
    }

    public AuthResponse getUsuario(String usuario, String clave) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsuario(usuario);

        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuario o clave incorrectos");
        }

        Usuario usuarioEncontrado = usuarioOpt.get();

        // Verifica la contraseña
        if (!passwordEncoder.matches(clave, usuarioEncontrado.getClave())) {
            throw new IllegalArgumentException("Usuario o clave incorrectos");
        }

        // Verifica si el usuario está inactivo (estado = false o 0)
        if (!usuarioEncontrado.getEstado()) {
            throw new IllegalArgumentException("El usuario está inactivo");
        }

        // Generar token JWT con el rol del usuario
        String token = jwtUtil.generateToken(usuarioEncontrado.getUsuario(), usuarioEncontrado.getRol().getNombre());

        // Crear y devolver la respuesta de autenticación
        return new AuthResponse(token, usuarioEncontrado.getUsuario(), usuarioEncontrado.getRol().getNombre());
    }
    
    public Usuario insertarUsuario(Usuario usuario) {
        // Validar que el campo "usuario" no sea nulo o vacío
        if (usuario.getUsuario() == null || usuario.getUsuario().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio.");
        }

        // Validar si ya existe un usuario con el mismo nombre
        Optional<Usuario> usuarioExistente = usuarioRepository.findByUsuario(usuario.getUsuario());

        if (usuarioExistente.isPresent()) {
            throw new IllegalArgumentException("El usuario ya existe.");
        }

        // Validar que el rol no sea nulo
        if (usuario.getRol() == null || usuario.getRol().getId() == null) {
            throw new IllegalArgumentException("El rol del usuario es obligatorio.");
        }

        log.info("Buscando rol con ID: {}", usuario.getRol().getId());
        // Buscar el rol en la base de datos
        Rol rol = rolRepository.findById(Long.valueOf(usuario.getRol().getId()))
        .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado con el ID: " + usuario.getRol().getId()));

        usuario.setRol(rol);

        // Cifrar la contraseña antes de guardar
        if (usuario.getClave() != null) {
            usuario.setClave(passwordEncoder.encode(usuario.getClave()));
        }

        return usuarioRepository.save(usuario);
    }
    
    

    // Borrado lógico
    public void borrarUsuario(Long id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setEstado(false); // Cambia el estado a 0 (false)
            usuarioRepository.save(usuario); // Actualiza el registro
        } else {
            throw new IllegalArgumentException("Usuario no encontrado con id: " + id);
        }
    }

    // Actualizar por Id
    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + id));
    
        // Solo permite actualizaciones si el usuario está activo
        if (!usuarioExistente.getEstado()) {
            if (usuarioActualizado.getEstado() != null && usuarioActualizado.getEstado()) {
                usuarioExistente.setEstado(true); // Permitir activar el usuario
            } else {
                throw new IllegalArgumentException("No se puede actualizar un usuario inactivo.");
            }
        }
    
        // Actualizar los campos proporcionados
        if (usuarioActualizado.getNombre() != null) {
            usuarioExistente.setNombre(usuarioActualizado.getNombre());
        }
        if (usuarioActualizado.getEmail() != null) {
            usuarioExistente.setEmail(usuarioActualizado.getEmail());
        }
        if (usuarioActualizado.getTelefono() != null) {
            usuarioExistente.setTelefono(usuarioActualizado.getTelefono());
        }
        if (usuarioActualizado.getClave() != null) {
            usuarioExistente.setClave(passwordEncoder.encode(usuarioActualizado.getClave()));
        }
        if (usuarioActualizado.getRol() != null) {
            Rol rol = rolRepository.findById(usuarioActualizado.getRol().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado."));
            usuarioExistente.setRol(rol);
        }
    
        usuarioExistente.setFechaActualizacion(LocalDateTime.now()); // Actualiza la fecha de actualización
    
        // Guarda los cambios
        return usuarioRepository.save(usuarioExistente);
    }
    
}

