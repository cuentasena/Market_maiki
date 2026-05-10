package com.mifichafavorita.gestionusuarios.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

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

    public List<UserResponseDTO> listUsers() {
        List<Users> usersFound = userRepository.findAll();
        List<UserResponseDTO> response = new ArrayList<>();

        for (Users users : usersFound) {
            UserResponseDTO user = new UserResponseDTO();
            user.setId(users.getId());
            user.setName(users.getName());
            user.setEmail(users.getEmail());
            user.setAge(users.getAge());
            user.setRol(users.getRolId());
            response.add(user);
        }

        return response;
    }
}
