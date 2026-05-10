package com.mifichafavorita.gestionusuarios.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Entidad JPA que representa un usuario de la aplicación.
 * Se mapea a la tabla {@code Users} en MySQL.
 */
@Entity
@Data
@Table(name = "Users")
public class Users {

    /** Identificador único autogenerado. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /** Nombre completo o visible del usuario. */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /** Correo electrónico (usado también como identificador en el login). Debe ser único. */
    @Column(name = "email", nullable = false, length = 255)
    private String email;

    /** Contraseña almacenada con hash (BCrypt), nunca en texto plano. */
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    /** Edad del usuario. */
    @Column(name = "age", nullable = false)
    private Long age;

    /**
     * Clave foránea lógica al rol ({@code 1} = CAJERO, {@code 2} = USUARIO).
     * Coincide con {@link com.mifichafavorita.gestionusuarios.enums.RolEnum#getId()}.
     */
    @Column(name = "rol_id", nullable = false)
    private Long rolId;
}
