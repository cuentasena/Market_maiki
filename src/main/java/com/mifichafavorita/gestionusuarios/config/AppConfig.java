package com.mifichafavorita.gestionusuarios.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuración general de beans de la aplicación que no pertenecen a filtros ni seguridad web completa.
 */
@Configuration
public class AppConfig {

    /**
     * Bean para codificar y verificar contraseñas con BCrypt (usado en registro y actualización de cuenta).
     *
     * @return implementación {@link BCryptPasswordEncoder}
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
