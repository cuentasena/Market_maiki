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

/**
 * API REST de usuarios: listado para personal de caja y gestión de perfil/cuenta según rol.
 * Requiere JWT válido (excepto rutas bajo {@code /auth}). Los atributos {@code userId} y {@code rolId}
 * los coloca {@link com.mifichafavorita.gestionusuarios.filter.JwtValidationFilter}.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    /** Servicio de consulta y actualización de usuarios. */
    private final UserService userService;

    /**
     * Lista completa de usuarios en formato DTO.
     * Solo el rol {@link RolEnum#CAJERO} puede invocarla; si no, HTTP 403 con mensaje JSON.
     *
     * @param httpRequest petición con atributos {@code rolId} inyectados por el filtro JWT
     * @return lista en éxito (302 FOUND); en error de permiso u operación, {@link HttpGlobalResponse} u objeto de error
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
     * Datos del usuario identificado por el token (cajero o usuario final).
     *
     * @param httpRequest debe incluir {@code userId} del JWT
     * @return perfil envuelto en {@link HttpGlobalResponse}
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
     * Vista “mi cuenta” exclusiva del rol {@link RolEnum#USUARIO} (cliente).
     *
     * @param httpRequest petición con {@code rolId} y {@code userId}
     * @return mismos datos que {@link #miPerfil(HttpServletRequest)} pero con control de rol USUARIO
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
     * Actualización parcial de la cuenta del cliente (nombre, edad, contraseña opcional).
     * Solo {@link RolEnum#USUARIO}; el cuerpo se valida con {@code @Valid}.
     *
     * @param httpRequest petición con {@code rolId} y {@code userId}
     * @param request     campos opcionales a modificar
     * @return usuario actualizado en {@code data}
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
