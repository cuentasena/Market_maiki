package com.mifichafavorita.gestionusuarios.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mifichafavorita.gestionusuarios.dto.HttpGlobalResponse;
import com.mifichafavorita.gestionusuarios.dto.JwtDTO;
import com.mifichafavorita.gestionusuarios.dto.LoginRequestDTO;
import com.mifichafavorita.gestionusuarios.dto.RegisterRequestDTO;
import com.mifichafavorita.gestionusuarios.dto.RegisterResponseDTO;
import com.mifichafavorita.gestionusuarios.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Endpoints públicos y semi-públicos de autenticación bajo el prefijo {@code /auth}.
 * El filtro JWT no intercepta estas rutas salvo {@code /refresh}, que exige header Bearer.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    /** Servicio que concentra registro, login y refresco de token. */
    private final AuthService authService;

    /**
     * Alta de usuario. El cuerpo se valida con Bean Validation ({@code @Valid}).
     *
     * @param request datos de registro (nombre, correo, contraseña, edad, rol opcional)
     * @return mensaje en {@link RegisterResponseDTO}; HTTP 202 si el servicio respondió sin excepción
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        try {
            RegisterResponseDTO response = authService.register(request);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            RegisterResponseDTO err = new RegisterResponseDTO();
            err.setMessage("Error al registrar");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }
    }

    /**
     * Login con correo y contraseña; respuesta incluye JWT si las credenciales son correctas.
     *
     * @param request credenciales validadas con {@code @Valid}
     * @return {@link HttpGlobalResponse} con mensaje y opcionalmente {@link JwtDTO}
     */
    @PostMapping("/login")
    public ResponseEntity<HttpGlobalResponse<JwtDTO>> login(@Valid @RequestBody LoginRequestDTO request) {
        try {
            HttpGlobalResponse<JwtDTO> response = authService.login(request);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            HttpGlobalResponse<JwtDTO> err = new HttpGlobalResponse<>();
            err.setMessage("Error en el inicio de sesión");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }
    }

    /**
     * Renueva el JWT enviando el actual en {@code Authorization: Bearer &lt;token&gt;}.
     *
     * @param request petición HTTP para leer el header Authorization
     * @return nuevo token en {@link JwtDTO} si la operación es válida
     */
    @GetMapping("/refresh")
    public ResponseEntity<JwtDTO> refreshToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String token = authHeader.replaceFirst("Bearer ", "");

        JwtDTO response = new JwtDTO();

        try {
            response = authService.refreshToken(token);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
