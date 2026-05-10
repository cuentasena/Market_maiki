package com.mifichafavorita.gestionusuarios.dto;

import lombok.Data;

@Data
public class ActualizarCuentaRequestDTO {
    /**
     * Nuevo nombre (opcional)
     */
    private String name;

    /**
     * Nueva edad (opcional)
     */
    private Long age;

    /**
     * Nueva contraseña (opcional); si se envía y no está vacía, reemplaza la actual (hash BCrypt).
     */
    private String password;
}
