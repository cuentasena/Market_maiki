package com.mifichafavorita.gestionusuarios.enums;

/**
 * Enumeración de roles del sistema. Los valores numéricos ({@code id}) deben coincidir
 * con la columna {@code rol_id} en la tabla {@code Users} de la base de datos.
 */
public enum RolEnum {
    /** Rol con permiso para operaciones de caja (por ejemplo procesar pagos). Id = 1. */
    CAJERO(1L),
    /** Rol de cliente final: gestiona su propia cuenta. Id = 2. */
    USUARIO(2L);

    private final Long id;

    RolEnum(Long id) {
        this.id = id;
    }

    /**
     * Identificador persistido en BD para este rol.
     *
     * @return id numérico del rol (1 o 2)
     */
    public Long getId() {
        return id;
    }

    /**
     * Indica si el {@code rolId} recibido (por ejemplo desde el JWT o la BD) corresponde a este rol.
     *
     * @param rolId identificador de rol a comparar; puede ser {@code null}
     * @return {@code true} si coincide con el id de este enum
     */
    public boolean coincide(Long rolId) {
        return rolId != null && rolId.equals(id);
    }
}
