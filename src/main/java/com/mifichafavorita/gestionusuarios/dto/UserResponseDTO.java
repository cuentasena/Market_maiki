package com.mifichafavorita.gestionusuarios.dto;

import lombok.Data;

/**
 * Vista de usuario segura para exponer en la API: no incluye la contraseña.
 * Sirve para listados, perfil y cuenta propia.
 */
@Data
public class UserResponseDTO {
    /** Identificador del usuario en base de datos. */
    private Long id;

    /** Nombre visible. */
    private String name;

    /** Correo electrónico. */
    private String email;

    /** Edad. */
    private Long age;

    /**
     * Identificador numérico del rol ({@code 1} = CAJERO, {@code 2} = USUARIO), alineado con {@code rol_id} en BD.
     */
    private Long rol;
}
