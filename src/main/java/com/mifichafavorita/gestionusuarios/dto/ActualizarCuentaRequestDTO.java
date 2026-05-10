package com.mifichafavorita.gestionusuarios.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ActualizarCuentaRequestDTO {
    /**
     * Nuevo nombre (opcional)
     */
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String name;

    /**
     * Nueva edad (opcional)
     */
    @Min(value = 1, message = "La edad debe ser al menos 1")
    @Max(value = 120, message = "La edad no es válida")
    private Long age;

    /**
     * Nueva contraseña (opcional); si se envía y no está vacía, reemplaza la actual (hash BCrypt).
     */
    @Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres")
    private String password;
}
