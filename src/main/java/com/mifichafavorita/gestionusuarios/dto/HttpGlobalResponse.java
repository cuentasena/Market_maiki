package com.mifichafavorita.gestionusuarios.dto;

import lombok.Data;

/**
 * Envoltorio estándar de respuesta HTTP usado en varios endpoints para mantener el mismo formato JSON:
 * un mensaje legible para el cliente y un campo {@code data} tipado.
 *
 * @param <T> tipo del cuerpo útil (por ejemplo {@link JwtDTO}, {@link UserResponseDTO}, lista de errores, etc.)
 */
@Data
public class HttpGlobalResponse<T> {
    /** Carga útil opcional (token, usuario, mapa de errores de validación, etc.). */
    private T data;

    /** Mensaje corto describiendo el resultado (éxito o causa del fallo). */
    private String message;
}
