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
import jakarta.validation.Valid;
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
     * Lista de usuarios: solo CAJERO. Ante 403/400 el cuerpo lleva mensaje en JSON (HttpGlobalResponse).
     */
    @GetMapping("/list-users")
    public ResponseEntity<Object> listUsers(HttpServletRequest httpRequest) {
        try {
            Long rolId = (Long) httpRequest.getAttribute("rolId");
            if (!RolEnum.CAJERO.coincide(rolId)) {
                HttpGlobalResponse<String> denied = new HttpGlobalResponse<>();
                denied.setMessage("Solo el rol CAJERO puede listar usuarios");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(denied);
            }
            List<UserResponseDTO> response = userService.listUsers();
            return ResponseEntity.status(HttpStatus.FOUND).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            HttpGlobalResponse<String> err = new HttpGlobalResponse<>();
            err.setMessage("No se pudo obtener la lista de usuarios");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }
    }

    /**
     * Perfil del usuario autenticado (CAJERO o USUARIO): mismos datos que en respuestas de usuario, sin listar a otros.
     */
    @GetMapping("/mi-perfil")
    public ResponseEntity<HttpGlobalResponse<UserResponseDTO>> miPerfil(HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            return userService.obtenerUsuarioPorId(userId)
                    .map(u -> {
                        HttpGlobalResponse<UserResponseDTO> ok = new HttpGlobalResponse<>();
                        ok.setMessage("Tu perfil");
                        ok.setData(u);
                        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ok);
                    })
                    .orElseGet(() -> {
                        HttpGlobalResponse<UserResponseDTO> nf = new HttpGlobalResponse<>();
                        nf.setMessage("Usuario no encontrado");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(nf);
                    });
        } catch (Exception e) {
            e.printStackTrace();
            HttpGlobalResponse<UserResponseDTO> err = new HttpGlobalResponse<>();
            err.setMessage("Error al consultar el perfil");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }
    }

    /**
     * Consultar datos propios: solo USUARIO (vista “cuenta cliente”).
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
                    .orElseGet(() -> {
                        HttpGlobalResponse<UserResponseDTO> nf = new HttpGlobalResponse<>();
                        nf.setMessage("Usuario no encontrado");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(nf);
                    });
        } catch (Exception e) {
            e.printStackTrace();
            HttpGlobalResponse<UserResponseDTO> err = new HttpGlobalResponse<>();
            err.setMessage("Error en la solicitud");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }
    }

    /**
     * Actualizar nombre, edad y/o contraseña de la cuenta propia: solo USUARIO.
     */
    @PatchMapping("/mi-cuenta")
    public ResponseEntity<HttpGlobalResponse<UserResponseDTO>> actualizarMiCuenta(HttpServletRequest httpRequest,
            @Valid @RequestBody ActualizarCuentaRequestDTO request) {
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
                    .orElseGet(() -> {
                        HttpGlobalResponse<UserResponseDTO> nf = new HttpGlobalResponse<>();
                        nf.setMessage("Usuario no encontrado");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(nf);
                    });
        } catch (Exception e) {
            e.printStackTrace();
            HttpGlobalResponse<UserResponseDTO> err = new HttpGlobalResponse<>();
            err.setMessage("Error al actualizar la cuenta");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }
    }
}
