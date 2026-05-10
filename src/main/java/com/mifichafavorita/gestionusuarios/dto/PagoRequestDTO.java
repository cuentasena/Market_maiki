package com.mifichafavorita.gestionusuarios.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * Cuerpo del endpoint {@code POST /pagos/procesar}. Describe un cobro de ejemplo (demo sin tabla de transacciones).
 */
@Data
public class PagoRequestDTO {
    /** Importe cobrado; debe ser un número positivo. */
    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor que cero")
    private Double monto;

    /** Texto libre opcional (producto, referencia, etc.). */
    private String concepto;
}
