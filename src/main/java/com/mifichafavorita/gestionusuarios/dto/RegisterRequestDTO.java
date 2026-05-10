package com.mifichafavorita.gestionusuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Datos que el cliente envía para crear una cuenta. Las anotaciones aplican validación antes de llegar al servicio.
 */
@Data
public class RegisterRequestDTO {
    /** Nombre completo u obligatorio para mostrar en la aplicación. */
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String name;

    /** Correo único en el sistema; también sirve para iniciar sesión. */
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe ser un correo válido")
    private String email;

    /** Contraseña en claro solo en tránsito; el servidor la guarda hasheada (BCrypt). */
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres")
    private String password;

    /** Edad numérica dentro de un rango razonable. */
    @NotNull(message = "La edad es obligatoria")
    @Min(value = 1, message = "La edad debe ser al menos 1")
    @Max(value = 120, message = "La edad no es válida")
    private Long age;

    /**
     * Rol deseado: {@code 1} = CAJERO, {@code 2} = USUARIO.
     * Si se omite, el backend asigna USUARIO.
     */
    private Long rol;
}
