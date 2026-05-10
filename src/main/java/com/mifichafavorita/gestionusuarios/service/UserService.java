package com.mifichafavorita.gestionusuarios.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mifichafavorita.gestionusuarios.dto.ActualizarCuentaRequestDTO;
import com.mifichafavorita.gestionusuarios.dto.UserResponseDTO;
import com.mifichafavorita.gestionusuarios.entity.Users;
import com.mifichafavorita.gestionusuarios.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Operaciones de lectura y actualización de usuarios para exponer en la API (listados y perfil propio).
 * No implementa registro ni login; eso está en {@link AuthService}.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    /** Repositorio JPA de usuarios. */
    private final UserRepository userRepository;

    /** Para hashear contraseña cuando el usuario actualiza su cuenta. */
    private final PasswordEncoder passwordEncoder;

    /**
     * Obtiene todos los registros de usuarios y los convierte a DTO (sin contraseña).
     *
     * @return lista para uso del endpoint de listado (restringido a CAJERO en el controlador)
     */
    public List<UserResponseDTO> listUsers() {
        List<Users> usersFound = userRepository.findAll();
        List<UserResponseDTO> response = new ArrayList<>();

        for (Users users : usersFound) {
            response.add(mapearUsuario(users));
        }

        return response;
    }

    /**
     * Busca un usuario por id y lo devuelve como DTO público.
     *
     * @param id clave primaria
     * @return optional vacío si no existe
     */
    public Optional<UserResponseDTO> obtenerUsuarioPorId(Long id) {
        return userRepository.findById(id).map(this::mapearUsuario);
    }

    /**
     * Actualiza solo los campos enviados en el DTO (parcial). La contraseña, si viene no vacía, se re-hashea.
     *
     * @param id    usuario autenticado (coincide con token)
     * @param datos nombre, edad y/o contraseña nueva
     * @return usuario actualizado como DTO, o vacío si el id no existe
     */
    public Optional<UserResponseDTO> actualizarMiCuenta(Long id, ActualizarCuentaRequestDTO datos) {
        Optional<Users> opt = userRepository.findById(id);
        if (opt.isEmpty()) {
            return Optional.empty();
        }
        Users u = opt.get();
        if (datos.getName() != null && !datos.getName().isBlank()) {
            u.setName(datos.getName());
        }
        if (datos.getAge() != null) {
            u.setAge(datos.getAge());
        }
        if (datos.getPassword() != null && !datos.getPassword().isBlank()) {
            u.setPassword(passwordEncoder.encode(datos.getPassword()));
        }
        userRepository.save(u);
        return Optional.of(mapearUsuario(u));
    }

    /**
     * Copia campos seguros de la entidad al DTO de respuesta (nunca incluye {@code password}).
     *
     * @param users entidad persistida
     * @return DTO para JSON
     */
    private UserResponseDTO mapearUsuario(Users users) {
        UserResponseDTO user = new UserResponseDTO();
        user.setId(users.getId());
        user.setName(users.getName());
        user.setEmail(users.getEmail());
        user.setAge(users.getAge());
        user.setRol(users.getRolId());
        return user;
    }
}
