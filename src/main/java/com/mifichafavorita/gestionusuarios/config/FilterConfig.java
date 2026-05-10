package com.mifichafavorita.gestionusuarios.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mifichafavorita.gestionusuarios.filter.JwtValidationFilter;

/**
 * Registra beans relacionados con filtros servlet.
 * Al arrancar, Spring lee esta clase y publica el filtro JWT en el contenedor.
 */
@Configuration
public class FilterConfig {

    /**
     * Registra {@link JwtValidationFilter} para todas las URLs ({@code /*}) con orden de ejecución 0.
     *
     * @param jwtValidationFilter filtro inyectado por Spring (componente)
     * @return bean que asocia el filtro al DispatcherServlet
     */
    @Bean
    FilterRegistrationBean<JwtValidationFilter> jwtFilter(JwtValidationFilter jwtValidationFilter) {
        // Creamos un contenedor de registro del bean para el filtro
        FilterRegistrationBean<JwtValidationFilter> registrationBean = new FilterRegistrationBean<>();

        // Es decirle a Spring que este filtro es el que quiero que trabaje
        registrationBean.setFilter(jwtValidationFilter);

        // Definir el alcance del filtro, quiero que revise todas las peticiones que entren en mi app
        registrationBean.addUrlPatterns("/*");

        // Establecemos la prioridad de ejecucion de los filtros
        registrationBean.setOrder(0);

        // Retornamos el bean configurado para que spring lo guarde en su contexto (inyección)
        return registrationBean;
    }
}
