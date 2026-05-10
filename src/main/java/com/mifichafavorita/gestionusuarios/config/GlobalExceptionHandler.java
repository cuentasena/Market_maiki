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

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Errores @Valid en cuerpos JSON (@RequestBody).
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
     * JSON mal formado o tipo incompatible.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<HttpGlobalResponse<String>> handleNotReadable(HttpMessageNotReadableException ex) {
        HttpGlobalResponse<String> body = new HttpGlobalResponse<>();
        body.setMessage("Cuerpo JSON inválido o incompleto");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
