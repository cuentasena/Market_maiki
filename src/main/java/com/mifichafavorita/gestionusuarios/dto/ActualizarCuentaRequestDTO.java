package com.mifichafavorita.gestionusuarios.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Actualización parcial de la cuenta del usuario final ({@code PATCH /users/mi-cuenta}).
 * Todos los campos son opcionales: solo se aplican los que el cliente envía.
 */
@Data
public class ActualizarCuentaRequestDTO {
    /** Nuevo nombre; si viene vacío o null, no se cambia el guardado. */
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String name;

    /** Nueva edad; {@code null} significa “no modificar”. */
    @Min(value = 1, message = "La edad debe ser al menos 1")
    @Max(value = 120, message = "La edad no es válida")
    private Long age;

    /**
     * Nueva contraseña en claro solo en tránsito; si tiene valor no vacío se persiste con BCrypt.
     * Las restricciones de tamaño aplican cuando el campo está presente en el JSON.
     */
    @Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres")
    private String password;
}
