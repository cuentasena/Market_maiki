package com.mifichafavorita.gestionusuarios.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mifichafavorita.gestionusuarios.dto.UserResponseDTO;
import com.mifichafavorita.gestionusuarios.service.UserService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    /**
     * Servicio de usuarios
     */
    private final UserService userService;

    @GetMapping("/list-users")
    public ResponseEntity<List<UserResponseDTO>> listUsers() {
        try {
            List<UserResponseDTO> response = userService.listUsers();
            return ResponseEntity.status(HttpStatus.FOUND).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
