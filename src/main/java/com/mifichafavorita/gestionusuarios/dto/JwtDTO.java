package com.mifichafavorita.gestionusuarios.dto;

import lombok.Data;

/**
 * Transporta el token JWT como texto para serializarlo en JSON tras el login o el refresco.
 */
@Data
public class JwtDTO {
    /** Cadena del JSON Web Token (sin prefijo {@code Bearer}); el cliente lo envía en el header Authorization. */
    private String jwt;
}
