package com.mifichafavorita.gestionusuarios.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mifichafavorita.gestionusuarios.dto.HttpGlobalResponse;
import com.mifichafavorita.gestionusuarios.dto.PagoRequestDTO;
import com.mifichafavorita.gestionusuarios.enums.RolEnum;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Operaciones de negocio relacionadas con cobros en punto de venta.
 * El procesamiento simulado solo está permitido para el rol {@link RolEnum#CAJERO}.
 */
@RestController
@RequestMapping("/pagos")
@RequiredArgsConstructor
public class PagosController {

    /**
     * Registra un pago de demostración (no persiste transacción en otra tabla).
     * Exige JWT y {@code rolId} correspondiente a cajero.
     *
     * @param httpRequest petición con {@code rolId} del filtro JWT
     * @param request     monto obligatorio positivo y concepto opcional (validado con {@code @Valid})
     * @return confirmación en {@link HttpGlobalResponse}; 403 si el rol no es cajero
     */
    @PostMapping("/procesar")
    public ResponseEntity<HttpGlobalResponse<String>> procesarPago(HttpServletRequest httpRequest,
            @Valid @RequestBody PagoRequestDTO request) {
        try {
            Long rolId = (Long) httpRequest.getAttribute("rolId");

            if (!RolEnum.CAJERO.coincide(rolId)) {
                HttpGlobalResponse<String> denied = new HttpGlobalResponse<>();
                denied.setMessage("No tiene permiso para procesar pagos (solo cajero)");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(denied);
            }

            HttpGlobalResponse<String> ok = new HttpGlobalResponse<>();
            ok.setMessage("Pago procesado correctamente");
            ok.setData("Monto: " + request.getMonto()
                    + (request.getConcepto() != null ? " | Concepto: " + request.getConcepto() : ""));
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(ok);
        } catch (Exception e) {
            e.printStackTrace();
            HttpGlobalResponse<String> err = new HttpGlobalResponse<>();
            err.setMessage("Error al procesar el pago");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }
    }
}
