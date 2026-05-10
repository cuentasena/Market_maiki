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
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    /**
     * Servicvio de autenticación
     */
    private final AuthService authService;

    /**
     * Registro de usuario
     * 
     * @param request
     * @return RegisterResponseDTO
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody RegisterRequestDTO request) {
        try {
            RegisterResponseDTO response = authService.register(request);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Inicio de sesion del usuario
     * 
     * @param request
     * @return HttpGlobalResponse<JwtDTO>
     */
    @PostMapping("/login")
    public ResponseEntity<HttpGlobalResponse<JwtDTO>> login(@RequestBody LoginRequestDTO request) {
        try {
            HttpGlobalResponse<JwtDTO> response = authService.login(request);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Refresco del jwt
     * 
     * @param request
     * @return JwtDTO
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
