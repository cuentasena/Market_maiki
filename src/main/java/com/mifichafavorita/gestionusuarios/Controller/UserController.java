package com.mifichafavorita.gestionusuarios.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mifichafavorita.gestionusuarios.dto.ActualizarCuentaRequestDTO;
import com.mifichafavorita.gestionusuarios.dto.HttpGlobalResponse;
import com.mifichafavorita.gestionusuarios.dto.UserResponseDTO;
import com.mifichafavorita.gestionusuarios.enums.RolEnum;
import com.mifichafavorita.gestionusuarios.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    /**
     * Servicio de usuarios
     */
    private final UserService userService;

    /**
     * Lista de usuarios: solo CAJERO.
     */
    @GetMapping("/list-users")
    public ResponseEntity<List<UserResponseDTO>> listUsers(HttpServletRequest httpRequest) {
        try {
            Long rolId = (Long) httpRequest.getAttribute("rolId");
            if (!RolEnum.CAJERO.coincide(rolId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
            List<UserResponseDTO> response = userService.listUsers();
            return ResponseEntity.status(HttpStatus.FOUND).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Consultar datos propios: solo USUARIO.
     */
    @GetMapping("/mi-cuenta")
    public ResponseEntity<HttpGlobalResponse<UserResponseDTO>> miCuenta(HttpServletRequest httpRequest) {
        try {
            Long rolId = (Long) httpRequest.getAttribute("rolId");
            if (!RolEnum.USUARIO.coincide(rolId)) {
                HttpGlobalResponse<UserResponseDTO> denied = new HttpGlobalResponse<>();
                denied.setMessage("Esta ruta es solo para usuarios con rol USUARIO");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(denied);
            }
            Long userId = (Long) httpRequest.getAttribute("userId");
            return userService.obtenerUsuarioPorId(userId)
                    .map(u -> {
                        HttpGlobalResponse<UserResponseDTO> ok = new HttpGlobalResponse<>();
                        ok.setMessage("Datos de la cuenta");
                        ok.setData(u);
                        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ok);
                    })
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Actualizar nombre/edad de la cuenta propia: solo USUARIO.
     */
    @PatchMapping("/mi-cuenta")
    public ResponseEntity<HttpGlobalResponse<UserResponseDTO>> actualizarMiCuenta(HttpServletRequest httpRequest,
            @RequestBody ActualizarCuentaRequestDTO request) {
        try {
            Long rolId = (Long) httpRequest.getAttribute("rolId");
            if (!RolEnum.USUARIO.coincide(rolId)) {
                HttpGlobalResponse<UserResponseDTO> denied = new HttpGlobalResponse<>();
                denied.setMessage("Solo el rol USUARIO puede modificar su cuenta aquí");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(denied);
            }
            Long userId = (Long) httpRequest.getAttribute("userId");
            return userService.actualizarMiCuenta(userId, request)
                    .map(u -> {
                        HttpGlobalResponse<UserResponseDTO> ok = new HttpGlobalResponse<>();
                        ok.setMessage("Cuenta actualizada");
                        ok.setData(u);
                        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ok);
                    })
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
