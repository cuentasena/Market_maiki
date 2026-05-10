package com.mifichafavorita.gestionusuarios.enums;

public enum RolEnum {
    CAJERO(1L),
    USUARIO(2L);

    private final Long id;

    RolEnum(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    /** Compara el rol del token o BD con este enum. */
    public boolean coincide(Long rolId) {
        return rolId != null && rolId.equals(id);
    }
}
