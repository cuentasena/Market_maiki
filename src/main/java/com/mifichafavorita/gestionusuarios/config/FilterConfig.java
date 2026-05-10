package com.mifichafavorita.gestionusuarios.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mifichafavorita.gestionusuarios.filter.JwtValidationFilter;

// Fabrica de beans (springboot al arrancar lee la app para configurar el comportamiento de esta misma)
@Configuration
public class FilterConfig {
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
