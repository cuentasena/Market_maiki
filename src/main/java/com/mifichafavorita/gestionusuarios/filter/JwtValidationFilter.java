package com.mifichafavorita.gestionusuarios.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mifichafavorita.gestionusuarios.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Filtro que se ejecuta una vez por petición HTTP y valida el JWT enviado en el header {@code Authorization}.
 * Si el token es válido, deja en la petición los atributos {@code username}, {@code userId} y {@code rolId}
 * para que los controladores puedan aplicar reglas de negocio por rol.
 */
@RequiredArgsConstructor
@Log4j2
@Component
public class JwtValidationFilter extends OncePerRequestFilter {

    /** Servicio para validar y extraer datos del token. */
    private final JwtService jwtService;

    /**
     * Comprueba el header Bearer, valida el token y en caso exitoso continúa la cadena de filtros.
     *
     * @param request  petición entrante
     * @param response respuesta saliente
     * @param filterChain cadena de filtros siguiente
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException {
        // Busca y obtiene en el encabezado de la peticion el header llamado Authorization
        String authHeader = request.getHeader("Authorization");

        // Un token legal debe existir y empezar con Bearer
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Header Authorization is missing in the request\"}");
            return; // Cortamos el flujo y asi la petición no alcanza a llegar al controller
        }

        String token = authHeader.replaceFirst("Bearer ", "");

        try {
            if (jwtService.isTokenValid(token)) {
                String username = jwtService.extractUsername(token);
                Long userId = jwtService.extractUserId(token);
                Long rolId = jwtService.extractRolId(token);

                // Seteamos atributos en la peticion antes que llegue al controller para validarlos despues
                request.setAttribute("username", username);
                request.setAttribute("userId", userId);
                request.setAttribute("rolId", rolId);

                // Si todo sale bien, continuamos el flujo (otro filtro o el controller)
                filterChain.doFilter(request, response);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Token is invalid or expired\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Validation failed\"}");
            log.error("Error: " + e);
        }
    }

    /**
     * Rutas que no deben pasar por validación JWT (por ejemplo login y registro bajo {@code /auth}).
     *
     * @param request petición actual
     * @return {@code true} si esta URL se excluye del filtro
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        // Rutas públicas: no entran al filtro de validación del token
        return path.startsWith("/api/v1/auth");
    }
}
