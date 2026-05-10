package com.mifichafavorita.gestionusuarios.service;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Servicio responsable de crear, validar y leer tokens JWT (JJWT).
 * La clave y la vigencia se configuran en {@code application.yaml} bajo {@code security.jwt}.
 */
@Service
public class JwtService {
    /** Clave secreta en Base64 para firmar y verificar el JWT. */
    @Value("${security.jwt.secret-key}")
    String secretKey;

    /** Tiempo de vida del token en milisegundos (ej. 600000 = 10 minutos). */
    @Value("${security.jwt.token-expiration}")
    Long tokenExpiration;

    /**
     * Transforma la clave secreta de String (BASE64) a un obejto SecretKey
     * utilizable por la libreria
     * 
     * @return firma secreta
     */
    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Genera el JWT al iniciar sesión. Incluye claims {@code userId} y {@code rolId} en un solo mapa
     * para que ambos lleguen al cliente y al filtro.
     *
     * @param userId  identificador del usuario
     * @param rolId   identificador numérico del rol (1 CAJERO, 2 USUARIO)
     * @param username normalmente el correo; se guarda como {@code subject} del token
     * @return cadena JWT firmada
     */
    public String generateToken(Long userId, Long rolId, String username) {
        return Jwts.builder()
                .claims(Map.of("userId", userId, "rolId", rolId)) // claims personalizados (un solo mapa)
                .subject(username) // claim por defecto (a quien pertenece este token)
                .issuedAt(new Date()) // fecha de creacion
                .expiration(new Date(System.currentTimeMillis() + tokenExpiration)) // fecha de expiracion
                .signWith(getSignKey()) // Con que firmamos el token
                .compact(); // construye el String final
    }

    /**
     * Comprueba firma y formato del token (no comprueba solo expiración aquí; el parser puede lanzar si está vencido).
     *
     * @param token JWT recibido del cliente
     * @return {@code true} si el parser lo acepta como firma válida
     */
    public Boolean isTokenValid(String token) {
        try {
            // El parser intenta descifrar la firma del token y los compara
            Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lee el payload del token y aplica una función para obtener un claim tipado.
     *
     * @param <T>      tipo de retorno deseado
     * @param token    JWT válido
     * @param resolver función que recibe {@link io.jsonwebtoken.Claims}
     * @return valor extraído
     */
    public <T> T extractClaims(String token, Function<Claims, T> resolver) {
        final Claims claims = Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return resolver.apply(claims);
    }

    /**
     * Obtiene el {@code subject} del token (correo u otro identificador configurado al generar el token).
     *
     * @param token JWT
     * @return subject del token
     */
    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    /**
     * Obtiene el claim personalizado {@code userId}.
     *
     * @param token JWT
     * @return id del usuario
     */
    public Long extractUserId(String token) {
        return extractClaims(token, claims -> claims.get("userId", Long.class));
    }

    /**
     * Obtiene el claim personalizado {@code rolId}.
     *
     * @param token JWT
     * @return id numérico del rol
     */
    public Long extractRolId(String token) {
        return extractClaims(token, claims -> claims.get("rolId", Long.class));
    }

    /**
     * Emite un nuevo JWT con la misma información si el token actual sigue siendo parseable
     * (no permite refrescar si está expirado: lanza excepción).
     *
     * @param token JWT vigente
     * @return nuevo JWT con nueva fecha de expiración
     * @throws Exception si el token está expirado o es inválido
     */
    public String refreshToken(String token) throws Exception {
        Claims claims;

        try {
            claims = Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new Exception("Token is expired" + e.getMessage());
        } catch (JwtException e) {
            throw new Exception("Token is invalid" + e.getMessage());
        }

        // Generamos nuevo token con nueva expiracion
        return generateToken(claims.get("userId", Long.class), claims.get("rolId", Long.class), claims.getSubject());
    }
}