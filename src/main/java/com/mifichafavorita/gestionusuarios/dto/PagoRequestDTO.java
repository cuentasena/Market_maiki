package com.mifichafavorita.gestionusuarios.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PagoRequestDTO {
    /**
     * Monto del pago
     */
    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor que cero")
    private Double monto;

    /**
     * Concepto o descripción breve
     */
    private String concepto;
}
