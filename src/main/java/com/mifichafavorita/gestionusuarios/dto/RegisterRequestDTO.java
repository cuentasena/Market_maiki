package com.mifichafavorita.gestionusuarios.dto;

import lombok.Data;

@Data
public class RegisterRequestDTO {
    /**
     * Nombre del usuario
     */
    private String name;

    /**
     * Email del usuario
     */
    private String email;

    /**
     * Contraseña del usuario
     */
    private String password;

    /**
     * Edad del usuario
     */
    private Long age;

    /**
     * Rol: {@code 1} = CAJERO, {@code 2} = USUARIO. Si no se envía, se guarda como USUARIO.
     */
    private Long rol;
}
