package com.mifichafavorita.gestionusuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Credenciales para el endpoint {@code POST /auth/login}.
 */
@Data
public class LoginRequestDTO {
    /** Correo registrado previamente. */
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe ser un correo válido")
    private String email;

    /** Contraseña en texto plano; se compara contra el hash almacenado. */
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
