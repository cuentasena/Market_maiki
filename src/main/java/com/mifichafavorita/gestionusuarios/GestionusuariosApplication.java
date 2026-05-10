package com.mifichafavorita.gestionusuarios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicación Spring Boot.
 * {@link SpringBootApplication} activa autoconfiguración, escaneo de componentes y configuración MVC/JPA.
 */
@SpringBootApplication
public class GestionusuariosApplication {

    /**
     * Arranca el contexto de Spring y el servidor embebido (puerto definido en {@code application.yaml}).
     *
     * @param args argumentos de línea de comandos (no usados por defecto)
     */
    public static void main(String[] args) {
        SpringApplication.run(GestionusuariosApplication.class, args);
    }
}
