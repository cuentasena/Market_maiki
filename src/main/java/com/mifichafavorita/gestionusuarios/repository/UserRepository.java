package com.mifichafavorita.gestionusuarios.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mifichafavorita.gestionusuarios.entity.Users;

/**
 * Capa de acceso a datos para la entidad {@link Users}.
 * Spring Data JPA genera la implementación en tiempo de ejecución.
 */
@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    /**
     * Busca un usuario por su correo electrónico.
     *
     * @param email correo a buscar
     * @return usuario envuelto en {@link Optional} si existe
     */
    Optional<Users> findByEmail(String email);
}
