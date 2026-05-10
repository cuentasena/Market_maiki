package com.mifichafavorita.gestionusuarios.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mifichafavorita.gestionusuarios.dto.HttpGlobalResponse;

/**
 * Manejo centralizado de excepciones para todos los controladores REST.
 * Devuelve respuestas JSON uniformes ({@link HttpGlobalResponse}) ante errores de validación o JSON inválido.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Respuesta cuando fallan las validaciones {@code jakarta.validation} activadas con {@code @Valid}
     * en el cuerpo {@code @RequestBody}.
     *
     * @param ex excepción con detalle por campo ({@link MethodArgumentNotValidException#getBindingResult()})
     * @return HTTP 400 con mensaje global y mapa campo → mensaje en {@code data}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HttpGlobalResponse<Map<String, String>>> handleValidation(
            MethodArgumentNotValidException ex) {
        Map<String, String> detalle = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        err -> err.getField(),
                        err -> err.getDefaultMessage() != null ? err.getDefaultMessage() : "no válido",
                        (a, b) -> a,
                        LinkedHashMap::new));

        HttpGlobalResponse<Map<String, String>> body = new HttpGlobalResponse<>();
        body.setMessage("Datos de entrada no válidos");
        body.setData(detalle);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Respuesta cuando el JSON no se puede leer (sintaxis incorrecta, tipo incompatible, cuerpo vacío indebido).
     *
     * @param ex excepción de deserialización
     * @return HTTP 400 con mensaje descriptivo
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<HttpGlobalResponse<String>> handleNotReadable(HttpMessageNotReadableException ex) {
        HttpGlobalResponse<String> body = new HttpGlobalResponse<>();
        body.setMessage("Cuerpo JSON inválido o incompleto");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
