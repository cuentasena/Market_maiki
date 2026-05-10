package com.mifichafavorita.gestionusuarios.dto;

import lombok.Data;

/**
 * Respuesta simple del endpoint de registro: solo comunica el resultado mediante un mensaje.
 */
@Data
public class RegisterResponseDTO {
    /** Texto para el cliente (éxito, correo duplicado, rol inválido, etc.). */
    private String message;
}
