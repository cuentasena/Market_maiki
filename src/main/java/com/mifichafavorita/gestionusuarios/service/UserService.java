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

@Service
@RequiredArgsConstructor
public class UserService {

    /**
     * Repositorio del usuario
     */
    private final UserRepository userRepository;

    /**
     * Encriptación de contraseña al actualizar cuenta
     */
    private final PasswordEncoder passwordEncoder;

    public List<UserResponseDTO> listUsers() {
        List<Users> usersFound = userRepository.findAll();
        List<UserResponseDTO> response = new ArrayList<>();

        for (Users users : usersFound) {
            response.add(mapearUsuario(users));
        }

        return response;
    }

    public Optional<UserResponseDTO> obtenerUsuarioPorId(Long id) {
        return userRepository.findById(id).map(this::mapearUsuario);
    }

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
