package com.mifichafavorita.gestionusuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDTO {
    /**
     * Nombre del usuario
     */
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String name;

    /**
     * Email del usuario
     */
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe ser un correo válido")
    private String email;

    /**
     * Contraseña del usuario
     */
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres")
    private String password;

    /**
     * Edad del usuario
     */
    @NotNull(message = "La edad es obligatoria")
    @Min(value = 1, message = "La edad debe ser al menos 1")
    @Max(value = 120, message = "La edad no es válida")
    private Long age;

    /**
     * Rol: {@code 1} = CAJERO, {@code 2} = USUARIO. Si no se envía, se guarda como USUARIO.
     */
    private Long rol;
}
