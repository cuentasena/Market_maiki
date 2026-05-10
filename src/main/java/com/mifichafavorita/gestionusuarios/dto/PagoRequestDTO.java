package com.mifichafavorita.gestionusuarios.dto;

import lombok.Data;

@Data
public class PagoRequestDTO {
    /**
     * Monto del pago
     */
    private Double monto;

    /**
     * Concepto o descripción breve
     */
    private String concepto;
}
